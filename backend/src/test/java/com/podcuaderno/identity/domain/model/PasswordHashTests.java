package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PasswordHashTests {

    @Test
    void keepsTheEncodedValueButHidesItInToString() {
        PasswordHash hash = new PasswordHash("{argon2id}encoded");

        assertThat(hash.value()).isEqualTo("{argon2id}encoded");
        assertThat(hash).hasToString("PasswordHash[***]");
    }

    @Test
    void rejectsBlankAndNullValues() {
        assertThatThrownBy(() -> new PasswordHash(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PasswordHash(null)).isInstanceOf(NullPointerException.class);
    }
}
