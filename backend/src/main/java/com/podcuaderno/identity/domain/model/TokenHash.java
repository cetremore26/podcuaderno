package com.podcuaderno.identity.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/** SHA-256 of an opaque token in hexadecimal: the only form in which tokens are stored (ADR-0009). */
public record TokenHash(String value) {

    public TokenHash {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static TokenHash of(String rawToken) {
        Objects.requireNonNull(rawToken, "rawToken must not be null");
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return new TokenHash(HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
