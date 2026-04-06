package ratelimiter;

public final class ConsoleExternalResourceClient implements ExternalResourceClient {
    @Override
    public void call(ExternalCallContext context) {
        System.out.println("External call executed for customer=" + context.getCustomerId()
                + ", tenant=" + context.getTenantId()
                + ", provider=" + context.getProvider());
    }
}
