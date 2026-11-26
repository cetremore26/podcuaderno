package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.RateLimitExceededException;
import java.time.Duration;

/** Mapped to 429 with {@code Retry-After} (HU-02). */
public class TooManyLoginAttemptsException extends RateLimitExceededException {

    public TooManyLoginAttemptsException(Duration retryAfter) {
        super("Too many failed login attempts", retryAfter);
    }
}
