package com.podcuaderno.identity.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.identity.domain.model.EmailAddress;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class InMemoryLoginAttemptTrackerTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final EmailAddress ANA = new EmailAddress("ana@correo.com");

    private final InMemoryLoginAttemptTracker tracker = new InMemoryLoginAttemptTracker();

    @Test
    void returnsTheFailuresAfterTheGivenInstant() {
        tracker.recordFailure(ANA, NOW.minusSeconds(120));
        tracker.recordFailure(ANA, NOW);

        assertThat(tracker.failuresSince(ANA, NOW.minusSeconds(60))).containsExactly(NOW);
        assertThat(tracker.failuresSince(new EmailAddress("otro@correo.com"), NOW.minusSeconds(600)))
                .isEmpty();
    }

    @Test
    void forgetsFailuresOlderThanTheWindowWhenRecordingANewOne() {
        tracker.recordFailure(ANA, NOW.minus(Duration.ofMinutes(20)));
        tracker.recordFailure(ANA, NOW);

        assertThat(tracker.failuresSince(ANA, Instant.EPOCH)).containsExactly(NOW);
    }

    @Test
    void clearRemovesTheEmail() {
        tracker.recordFailure(ANA, NOW);

        tracker.clear(ANA);

        assertThat(tracker.failuresSince(ANA, Instant.EPOCH)).isEmpty();
        assertThat(tracker.trackedEmails()).isZero();
    }

    @Test
    void prunesStaleEmailsWhenTheMapGrowsTooLarge() {
        Instant old = NOW.minus(Duration.ofHours(1));
        for (int i = 0; i < InMemoryLoginAttemptTracker.PRUNE_THRESHOLD; i++) {
            tracker.recordFailure(new EmailAddress("user" + i + "@correo.com"), old);
        }

        tracker.recordFailure(ANA, NOW);

        assertThat(tracker.trackedEmails()).isOne();
        assertThat(tracker.failuresSince(ANA, Instant.EPOCH)).containsExactly(NOW);
    }
}
