package com.podcuaderno.identity.application.port.out;

import java.time.Instant;
import java.util.Objects;

/** A signed access token and when it expires. */
public record IssuedAccessToken(String value, Instant expiresAt) {

    public IssuedAccessToken {
        Objects.requireNonNull(value, "value must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    @Override
    public String toString() {
        return "IssuedAccessToken[***]";
    }
}
