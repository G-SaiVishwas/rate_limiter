package ratelimiter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class SlidingWindowCounterAlgorithm implements RateLimitingAlgorithm {
    private final TimeProvider timeProvider;
    private final Map<String, Map<String, SlidingBucket>> stateByKey = new ConcurrentHashMap<>();
    private final Map<String, Lock> lockByKey = new ConcurrentHashMap<>();

    public SlidingWindowCounterAlgorithm(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public boolean allow(String key, List<RateLimitRule> rules) {
        Lock lock = lockByKey.computeIfAbsent(key, ignored -> new ReentrantLock());
        lock.lock();
        try {
            Map<String, SlidingBucket> buckets = stateByKey.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>());
            long now = timeProvider.nowMillis();

            for (RateLimitRule rule : rules) {
                SlidingBucket bucket = buckets.computeIfAbsent(rule.getId(), ignored -> createBucket(now, rule));
                rollBucket(bucket, now, rule);
                double estimated = estimateCount(bucket, now, rule);
                if (estimated + 1.0 > rule.getLimit()) {
                    return false;
                }
            }

            for (RateLimitRule rule : rules) {
                SlidingBucket bucket = buckets.get(rule.getId());
                bucket.currentCount++;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private SlidingBucket createBucket(long now, RateLimitRule rule) {
        long windowMillis = rule.getWindowMillis();
        long windowStart = now - (now % windowMillis);
        return new SlidingBucket(windowStart, 0, 0);
    }

    private void rollBucket(SlidingBucket bucket, long now, RateLimitRule rule) {
        long windowMillis = rule.getWindowMillis();
        long currentWindowStart = now - (now % windowMillis);
        if (currentWindowStart == bucket.currentWindowStart) {
            return;
        }
        if (currentWindowStart == bucket.currentWindowStart + windowMillis) {
            bucket.previousCount = bucket.currentCount;
        } else {
            bucket.previousCount = 0;
        }
        bucket.currentCount = 0;
        bucket.currentWindowStart = currentWindowStart;
    }

    private double estimateCount(SlidingBucket bucket, long now, RateLimitRule rule) {
        long windowMillis = rule.getWindowMillis();
        long elapsedInCurrentWindow = now - bucket.currentWindowStart;
        double previousWeight = ((double) (windowMillis - elapsedInCurrentWindow)) / windowMillis;
        return bucket.currentCount + (bucket.previousCount * previousWeight);
    }

    private static final class SlidingBucket {
        private long currentWindowStart;
        private long currentCount;
        private long previousCount;

        private SlidingBucket(long currentWindowStart, long currentCount, long previousCount) {
            this.currentWindowStart = currentWindowStart;
            this.currentCount = currentCount;
            this.previousCount = previousCount;
        }
    }
}
