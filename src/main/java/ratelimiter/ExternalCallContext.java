package ratelimiter;

import java.util.Objects;

public final class ExternalCallContext {
    private final String customerId;
    private final String tenantId;
    private final String apiKey;
    private final String provider;

    public ExternalCallContext(String customerId, String tenantId, String apiKey, String provider) {
        this.customerId = customerId;
        this.tenantId = tenantId;
        this.apiKey = apiKey;
        this.provider = provider;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExternalCallContext)) {
            return false;
        }
        ExternalCallContext that = (ExternalCallContext) other;
        return Objects.equals(customerId, that.customerId)
                && Objects.equals(tenantId, that.tenantId)
                && Objects.equals(apiKey, that.apiKey)
                && Objects.equals(provider, that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, tenantId, apiKey, provider);
    }
}
