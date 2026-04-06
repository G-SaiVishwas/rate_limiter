package ratelimiter;

import java.util.Objects;

public final class InternalService {
    private final ExternalCallRateLimiter rateLimiter;
    private final ExternalResourceClient externalResourceClient;

    public InternalService(ExternalCallRateLimiter rateLimiter, ExternalResourceClient externalResourceClient) {
        this.rateLimiter = Objects.requireNonNull(rateLimiter);
        this.externalResourceClient = Objects.requireNonNull(externalResourceClient);
    }

    public String handleRequest(ExternalCallContext context, boolean requiresExternalCall) {
        if (!requiresExternalCall) {
            return "No external call needed";
        }
        if (!rateLimiter.allow(context)) {
            return "External call denied by rate limiter";
        }
        externalResourceClient.call(context);
        return "External call allowed";
    }
}
