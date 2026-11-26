package com.podcuaderno.identity.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Five failed logins within 15 minutes block the next attempts until the oldest of them leaves the window (HU-02). */
public final class LoginThrottle {

    public static final int MAX_FAILURES = 5;
    public static final Duration WINDOW = Duration.ofMinutes(15);

    private LoginThrottle() {}

    /** How long the caller must wait, or empty when a new attempt is allowed. */
    public static Optional<Duration> retryAfter(List<Instant> failures, Instant now) {
        Instant windowStart = now.minus(WINDOW);
        List<Instant> recent =
                failures.stream().filter(at -> at.isAfter(windowStart)).sorted().toList();
        if (recent.size() < MAX_FAILURES) {
            return Optional.empty();
        }
        Instant unblockedAt = recent.get(recent.size() - MAX_FAILURES).plus(WINDOW);
        return Optional.of(Duration.between(now, unblockedAt));
    }
}
