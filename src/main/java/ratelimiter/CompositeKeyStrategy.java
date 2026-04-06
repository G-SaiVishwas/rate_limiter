package ratelimiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CompositeKeyStrategy implements RateLimitKeyStrategy {
    private final List<RateLimitKeyStrategy> parts;

    public CompositeKeyStrategy(List<RateLimitKeyStrategy> parts) {
        if (parts == null || parts.isEmpty()) {
            throw new IllegalArgumentException("parts must not be empty");
        }
        this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
    }

    @Override
    public String createKey(ExternalCallContext context) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            RateLimitKeyStrategy strategy = parts.get(i);
            builder.append(Objects.requireNonNull(strategy.createKey(context)));
            if (i < parts.size() - 1) {
                builder.append("|");
            }
        }
        return builder.toString();
    }
}
