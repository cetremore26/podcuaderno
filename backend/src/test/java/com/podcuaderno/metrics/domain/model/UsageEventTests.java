package com.podcuaderno.metrics.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UsageEventTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    @Test
    void keepsTheTypeAndTheCharacterCount() {
        UserId user = UserId.newId();

        UsageEvent event = UsageEvent.occurred(user, UsageEventType.NOTEBOOK_CREATED, 120, NOW);

        assertThat(event.userId()).isEqualTo(user);
        assertThat(event.type()).isEqualTo(UsageEventType.NOTEBOOK_CREATED);
        assertThat(event.characters()).isEqualTo(120);
        assertThat(event.occurredAt()).isEqualTo(NOW);
        assertThat(event.id())
                .isNotEqualTo(UsageEvent.occurred(user, UsageEventType.NOTEBOOK_CREATED, 0, NOW)
                        .id());
    }

    @Test
    void charactersCannotBeNegative() {
        UserId user = UserId.newId();

        assertThatThrownBy(() -> UsageEvent.occurred(user, UsageEventType.USER_REGISTERED, -1, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void requiresItsMandatoryFields() {
        UsageEventId id = UsageEventId.newId();
        UserId user = UserId.newId();
        UsageEventType type = UsageEventType.USER_REGISTERED;

        assertThatThrownBy(() -> new UsageEvent(null, user, type, 0, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new UsageEvent(id, null, type, 0, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new UsageEvent(id, user, null, 0, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new UsageEvent(id, user, type, 0, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new UsageEventId(null)).isInstanceOf(NullPointerException.class);
    }
}
