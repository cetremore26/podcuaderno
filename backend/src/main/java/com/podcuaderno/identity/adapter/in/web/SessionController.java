package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.LogInUseCase;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Session", description = "Login, transparent renewal and logout (HU-02)")
class SessionController {

    private final LogInUseCase logIn;
    private final RefreshTokenCookies cookies;
    private final Clock clock;

    SessionController(LogInUseCase logIn, RefreshTokenCookies cookies, Clock clock) {
        this.logIn = logIn;
        this.cookies = cookies;
        this.clock = clock;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Start a session",
            description = "Returns a 15-minute access token and sets the refresh token in an HttpOnly cookie.")
    ResponseEntity<AccessTokenResponse> logIn(@Valid @RequestBody LogInRequest body) {
        return sessionResponse(logIn.logIn(body.toCommand()));
    }

    private ResponseEntity<AccessTokenResponse> sessionResponse(SessionTokens tokens) {
        Instant now = clock.instant();
        long expiresIn = Duration.between(now, tokens.accessTokenExpiresAt()).toSeconds();
        Duration cookieMaxAge = Duration.between(now, tokens.refreshTokenExpiresAt());
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookies.issue(tokens.refreshToken(), cookieMaxAge).toString())
                .body(AccessTokenResponse.bearer(tokens.accessToken(), expiresIn));
    }
}
