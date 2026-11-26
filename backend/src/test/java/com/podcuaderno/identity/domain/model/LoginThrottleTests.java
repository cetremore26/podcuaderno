package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class LoginThrottleTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private static List<Instant> failuresEveryMinuteEndingAt(Instant last, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> last.minus(Duration.ofMinutes(count - 1L - i)))
                .toList();
    }

    @Test
    void fourFailuresStillAllowAnAttempt() {
        assertThat(LoginThrottle.retryAfter(failuresEveryMinuteEndingAt(NOW, 4), NOW))
                .isEmpty();
    }

    @Test
    void fiveFailuresBlockUntilTheOldestLeavesTheWindow() {
        // Failures at 9:56, 9:57, 9:58, 9:59 and 10:00: blocked until 10:11.
        assertThat(LoginThrottle.retryAfter(failuresEveryMinuteEndingAt(NOW, 5), NOW))
                .contains(Duration.ofMinutes(11));
    }

    @Test
    void failuresOutsideTheWindowDoNotCount() {
        List<Instant> failures = new ArrayList<>(failuresEveryMinuteEndingAt(NOW, 4));
        failures.add(NOW.minus(Duration.ofMinutes(16)));

        assertThat(LoginThrottle.retryAfter(failures, NOW)).isEmpty();
    }

    @Test
    void usesTheMostRecentFiveFailuresWhenThereAreMore() {
        assertThat(LoginThrottle.retryAfter(failuresEveryMinuteEndingAt(NOW, 7), NOW))
                .contains(Duration.ofMinutes(11));
    }
}
