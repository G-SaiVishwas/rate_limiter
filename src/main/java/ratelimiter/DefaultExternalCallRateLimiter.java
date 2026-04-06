package ratelimiter;

import java.util.List;
import java.util.Objects;

public final class DefaultExternalCallRateLimiter implements ExternalCallRateLimiter {
    private final RateLimitingAlgorithm algorithm;
    private final RateLimitKeyStrategy keyStrategy;
    private final RateLimitRuleProvider ruleProvider;

    public DefaultExternalCallRateLimiter(
            RateLimitingAlgorithm algorithm,
            RateLimitKeyStrategy keyStrategy,
            RateLimitRuleProvider ruleProvider) {
        this.algorithm = Objects.requireNonNull(algorithm);
        this.keyStrategy = Objects.requireNonNull(keyStrategy);
        this.ruleProvider = Objects.requireNonNull(ruleProvider);
    }

    @Override
    public boolean allow(ExternalCallContext context) {
        String key = keyStrategy.createKey(context);
        List<RateLimitRule> rules = ruleProvider.getRules(context);
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        return algorithm.allow(key, rules);
    }
}
