package ratelimiter;

public final class TenantKeyStrategy implements RateLimitKeyStrategy {
    @Override
    public String createKey(ExternalCallContext context) {
        return valueOrDefault(context.getTenantId());
    }

    private String valueOrDefault(String value) {
        return value == null || value.isBlank() ? "unknown-tenant" : value;
    }
}
