package com.podcuaderno.identity.application.port.in;

import java.time.Instant;
import java.util.Objects;

/** Credentials of a session: the access token for the body and the refresh token for the cookie. */
public record SessionTokens(
        String accessToken, Instant accessTokenExpiresAt, String refreshToken, Instant refreshTokenExpiresAt) {

    public SessionTokens {
        Objects.requireNonNull(accessToken, "accessToken must not be null");
        Objects.requireNonNull(accessTokenExpiresAt, "accessTokenExpiresAt must not be null");
        Objects.requireNonNull(refreshToken, "refreshToken must not be null");
        Objects.requireNonNull(refreshTokenExpiresAt, "refreshTokenExpiresAt must not be null");
    }

    @Override
    public String toString() {
        return "SessionTokens[***]"; // never log tokens (docs/05 §5.8)
    }
}
