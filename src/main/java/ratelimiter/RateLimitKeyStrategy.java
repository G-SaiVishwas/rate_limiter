package ratelimiter;

public interface RateLimitKeyStrategy {
    String createKey(ExternalCallContext context);
}
