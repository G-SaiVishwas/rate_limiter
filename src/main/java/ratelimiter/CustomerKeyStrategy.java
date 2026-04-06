package ratelimiter;

public final class CustomerKeyStrategy implements RateLimitKeyStrategy {
    @Override
    public String createKey(ExternalCallContext context) {
        return valueOrDefault(context.getCustomerId());
    }

    private String valueOrDefault(String value) {
        return value == null || value.isBlank() ? "unknown-customer" : value;
    }
}
