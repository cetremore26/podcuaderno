package com.podcuaderno.identity.adapter.in.web;

import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/** Builds the refresh-token cookie: HttpOnly, SameSite=Strict and limited to the authentication endpoints (ADR-0009). */
@Component
@EnableConfigurationProperties(IdentityWebProperties.class)
class RefreshTokenCookies {

    static final String NAME = "pc_refresh";
    static final String PATH = "/api/v1/auth";
    private static final String SAME_SITE = "Strict";

    private final boolean secure;

    RefreshTokenCookies(IdentityWebProperties properties) {
        this.secure = properties.refreshCookieSecure();
    }

    ResponseCookie issue(String token, Duration maxAge) {
        return base(token).maxAge(maxAge).build();
    }

    ResponseCookie clear() {
        return base("").maxAge(Duration.ZERO).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(SAME_SITE)
                .path(PATH);
    }
}
