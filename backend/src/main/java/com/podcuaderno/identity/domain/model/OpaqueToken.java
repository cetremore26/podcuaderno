package com.podcuaderno.identity.domain.model;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/** Random secret sent to the user (verification link, refresh cookie). Only its {@link #hash()} is stored. */
public final class OpaqueToken {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int BYTES = 32;

    private final String value;

    private OpaqueToken(String value) {
        this.value = value;
    }

    public static OpaqueToken generate() {
        byte[] bytes = new byte[BYTES];
        RANDOM.nextBytes(bytes);
        return new OpaqueToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }

    public static OpaqueToken of(String rawValue) {
        return new OpaqueToken(Objects.requireNonNull(rawValue, "rawValue must not be null"));
    }

    public String value() {
        return value;
    }

    public TokenHash hash() {
        return TokenHash.of(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof OpaqueToken token && value.equals(token.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "OpaqueToken[***]"; // never log tokens (docs/05 §5.8)
    }
}
