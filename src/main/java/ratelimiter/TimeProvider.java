package ratelimiter;

public interface TimeProvider {
    long nowMillis();
}
