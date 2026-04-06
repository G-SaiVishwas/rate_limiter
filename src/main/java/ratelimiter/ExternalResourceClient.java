package ratelimiter;

public interface ExternalResourceClient {
    void call(ExternalCallContext context);
}
