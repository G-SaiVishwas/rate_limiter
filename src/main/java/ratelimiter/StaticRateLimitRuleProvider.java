package ratelimiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StaticRateLimitRuleProvider implements RateLimitRuleProvider {
    private final List<RateLimitRule> rules;

    public StaticRateLimitRuleProvider(List<RateLimitRule> rules) {
        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("rules must not be empty");
        }
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
    }

    @Override
    public List<RateLimitRule> getRules(ExternalCallContext context) {
        return rules;
    }
}
