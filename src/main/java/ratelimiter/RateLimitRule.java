package ratelimiter;

import java.time.Duration;
import java.util.Objects;

public final class RateLimitRule {
    private final String id;
    private final long limit;
    private final Duration window;

    public RateLimitRule(String id, long limit, Duration window) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        if (window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be positive");
        }
        this.id = id;
        this.limit = limit;
        this.window = window;
    }

    public String getId() {
        return id;
    }

    public long getLimit() {
        return limit;
    }

    public Duration getWindow() {
        return window;
    }

    public long getWindowMillis() {
        return window.toMillis();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RateLimitRule)) {
            return false;
        }
        RateLimitRule that = (RateLimitRule) other;
        return limit == that.limit && Objects.equals(id, that.id) && Objects.equals(window, that.window);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, limit, window);
    }
}
