package ratelimiter;

import java.util.List;

public interface RateLimitingAlgorithm {
    boolean allow(String key, List<RateLimitRule> rules);
}
