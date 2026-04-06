package ratelimiter;

public final class ProviderKeyStrategy implements RateLimitKeyStrategy {
    @Override
    public String createKey(ExternalCallContext context) {
        return valueOrDefault(context.getProvider());
    }

    private String valueOrDefault(String value) {
        return value == null || value.isBlank() ? "unknown-provider" : value;
    }
}
