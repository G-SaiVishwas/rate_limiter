package ratelimiter;

public interface ExternalCallRateLimiter {
    boolean allow(ExternalCallContext context);
}
