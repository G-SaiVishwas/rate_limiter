package ratelimiter;

import java.util.List;

public interface RateLimitRuleProvider {
    List<RateLimitRule> getRules(ExternalCallContext context);
}
