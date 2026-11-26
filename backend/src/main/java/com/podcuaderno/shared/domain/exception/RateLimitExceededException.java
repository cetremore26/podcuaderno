package com.podcuaderno.shared.domain.exception;

import java.time.Duration;
import java.util.Objects;

/** Mapped to 429 /errors/rate-limit with a {@code Retry-After} header. */
public abstract class RateLimitExceededException extends DomainException {

    private final Duration retryAfter;

    protected RateLimitExceededException(String message, Duration retryAfter) {
        super(message);
        this.retryAfter = Objects.requireNonNull(retryAfter, "retryAfter must not be null");
    }

    public Duration retryAfter() {
        return retryAfter;
    }
}
