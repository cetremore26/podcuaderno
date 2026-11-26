package com.podcuaderno.identity.domain.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserRegisteredTests {

    @Test
    void carriesOnlyTheUserIdAndTheInstant() {
        UserId user = UserId.newId();
        Instant now = Instant.parse("2026-12-14T10:00:00Z");

        UserRegistered event = new UserRegistered(user, now);

        assertThat(event.userId()).isEqualTo(user);
        assertThat(event.occurredAt()).isEqualTo(now);
        assertThatThrownBy(() -> new UserRegistered(null, now)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new UserRegistered(user, null)).isInstanceOf(NullPointerException.class);
    }
}
