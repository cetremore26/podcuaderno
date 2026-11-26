package com.podcuaderno.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserIdTests {

    @Test
    void rejectsNullValue() {
        assertThatThrownBy(() -> new UserId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void newIdGeneratesDistinctValues() {
        assertThat(UserId.newId()).isNotEqualTo(UserId.newId());
    }

    @Test
    void instancesWithTheSameValueAreEqual() {
        UUID value = UUID.randomUUID();

        assertThat(new UserId(value)).isEqualTo(new UserId(value)).hasSameHashCodeAs(new UserId(value));
    }
}
