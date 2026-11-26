package com.podcuaderno.identity.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.identity.domain.model.PasswordHash;
import org.junit.jupiter.api.Test;

class Argon2PasswordHasherTests {

    private final Argon2PasswordHasher hasher = new Argon2PasswordHasher();

    @Test
    void hashesWithArgon2idAndTheOwaspParameters() {
        PasswordHash hash = hasher.hash("contraseña-segura");

        assertThat(hash.value()).startsWith("{argon2id}$argon2id$v=19$m=19456,t=2,p=1$");
    }

    @Test
    void matchesOnlyTheOriginalPassword() {
        PasswordHash hash = hasher.hash("contraseña-segura");

        assertThat(hasher.matches("contraseña-segura", hash)).isTrue();
        assertThat(hasher.matches("contraseña-insegura", hash)).isFalse();
    }

    @Test
    void saltsEveryHash() {
        assertThat(hasher.hash("contraseña-segura")).isNotEqualTo(hasher.hash("contraseña-segura"));
    }

    @Test
    void dummyHashIsAValidHashThatMatchesNoCommonPassword() {
        PasswordHash dummy = hasher.dummyHash();

        assertThat(dummy.value()).startsWith("{argon2id}");
        assertThat(hasher.matches("dummy", dummy)).isFalse();
        assertThat(hasher.matches("", dummy)).isFalse();
        assertThat(hasher.dummyHash()).isSameAs(dummy);
    }
}
