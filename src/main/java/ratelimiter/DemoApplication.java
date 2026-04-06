package ratelimiter;

import java.time.Duration;
import java.util.List;

public final class DemoApplication {
    public static void main(String[] args) throws InterruptedException {
        ExternalCallContext t1Context = new ExternalCallContext("C1", "T1", "API-111", "maps-provider");

        List<RateLimitRule> rules = List.of(
                new RateLimitRule("per-minute", 5, Duration.ofMinutes(1)),
                new RateLimitRule("per-hour", 1000, Duration.ofHours(1))
        );

        RateLimitRuleProvider ruleProvider = new StaticRateLimitRuleProvider(rules);
        RateLimitKeyStrategy keyStrategy = new CompositeKeyStrategy(
                List.of(new TenantKeyStrategy(), new ProviderKeyStrategy())
        );

        TimeProvider timeProvider = new SystemTimeProvider();

        ExternalCallRateLimiter fixedWindowLimiter = new DefaultExternalCallRateLimiter(
                new FixedWindowCounterAlgorithm(timeProvider),
                keyStrategy,
                ruleProvider
        );

        ExternalCallRateLimiter slidingWindowLimiter = new DefaultExternalCallRateLimiter(
                new SlidingWindowCounterAlgorithm(timeProvider),
                keyStrategy,
                ruleProvider
        );

        ExternalResourceClient externalClient = new ConsoleExternalResourceClient();

        InternalService fixedWindowService = new InternalService(fixedWindowLimiter, externalClient);
        InternalService slidingWindowService = new InternalService(slidingWindowLimiter, externalClient);

        System.out.println("Fixed Window run");
        runScenario(fixedWindowService, t1Context);

        Thread.sleep(1200);

        System.out.println();
        System.out.println("Sliding Window run");
        runScenario(slidingWindowService, t1Context);
    }

    private static void runScenario(InternalService service, ExternalCallContext context) {
        System.out.println(service.handleRequest(context, false));
        for (int i = 1; i <= 6; i++) {
            String result = service.handleRequest(context, true);
            System.out.println("Attempt " + i + " -> " + result);
        }
    }
}
