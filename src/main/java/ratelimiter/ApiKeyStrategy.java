package ratelimiter;

public final class ApiKeyStrategy implements RateLimitKeyStrategy {
    @Override
    public String createKey(ExternalCallContext context) {
        return valueOrDefault(context.getApiKey());
    }

    private String valueOrDefault(String value) {
        return value == null || value.isBlank() ? "unknown-api-key" : value;
    }
}
