package com.podcuaderno.identity.domain.model;

import java.util.Objects;

/** Encoded password hash, including its algorithm prefix (for example {@code {argon2id}}). */
public record PasswordHash(String value) {

    public PasswordHash {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    @Override
    public String toString() {
        return "PasswordHash[***]";
    }
}
