package ratelimiter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class FixedWindowCounterAlgorithm implements RateLimitingAlgorithm {
    private final TimeProvider timeProvider;
    private final Map<String, Map<String, Bucket>> stateByKey = new ConcurrentHashMap<>();
    private final Map<String, Lock> lockByKey = new ConcurrentHashMap<>();

    public FixedWindowCounterAlgorithm(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public boolean allow(String key, List<RateLimitRule> rules) {
        Lock lock = lockByKey.computeIfAbsent(key, ignored -> new ReentrantLock());
        lock.lock();
        try {
            Map<String, Bucket> buckets = stateByKey.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>());
            long now = timeProvider.nowMillis();

            for (RateLimitRule rule : rules) {
                Bucket bucket = buckets.computeIfAbsent(rule.getId(), ignored -> new Bucket(now));
                alignWindow(bucket, now, rule);
                if (bucket.count + 1 > rule.getLimit()) {
                    return false;
                }
            }

            for (RateLimitRule rule : rules) {
                Bucket bucket = buckets.get(rule.getId());
                bucket.count++;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void alignWindow(Bucket bucket, long now, RateLimitRule rule) {
        long windowMillis = rule.getWindowMillis();
        long currentWindowStart = now - (now % windowMillis);
        if (bucket.windowStartMillis != currentWindowStart) {
            bucket.windowStartMillis = currentWindowStart;
            bucket.count = 0;
        }
    }

    private static final class Bucket {
        private long windowStartMillis;
        private long count;

        private Bucket(long now) {
            this.windowStartMillis = now;
            this.count = 0;
        }
    }
}
