package com.podcuaderno.identity.adapter.in.web;

import com.podcuaderno.identity.application.port.in.LogInUseCase;
import com.podcuaderno.identity.application.port.in.LogOutCommand;
import com.podcuaderno.identity.application.port.in.LogOutUseCase;
import com.podcuaderno.identity.application.port.in.RefreshSessionCommand;
import com.podcuaderno.identity.application.port.in.RefreshSessionUseCase;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.domain.exception.InvalidRefreshTokenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Session", description = "Login, transparent renewal and logout (HU-02)")
class SessionController {

    private final LogInUseCase logIn;
    private final RefreshSessionUseCase refreshSession;
    private final LogOutUseCase logOut;
    private final RefreshTokenCookies cookies;
    private final Clock clock;

    SessionController(
            LogInUseCase logIn,
            RefreshSessionUseCase refreshSession,
            LogOutUseCase logOut,
            RefreshTokenCookies cookies,
            Clock clock) {
        this.logIn = logIn;
        this.refreshSession = refreshSession;
        this.logOut = logOut;
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

    @PostMapping("/refresh")
    @Operation(
            summary = "Renew the access token",
            description = "Reads the refresh cookie, rotates it and returns a new access token. "
                    + "Reusing an already rotated token closes every session of its family.")
    ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue(name = RefreshTokenCookies.NAME, required = false) @Nullable String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        return sessionResponse(refreshSession.refresh(new RefreshSessionCommand(refreshToken)));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "End the session",
            description = "Revokes the refresh token family on the server and deletes the cookie. Always 204.")
    ResponseEntity<Void> logOut(
            @CookieValue(name = RefreshTokenCookies.NAME, required = false) @Nullable String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            logOut.logOut(new LogOutCommand(refreshToken));
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookies.clear().toString())
                .build();
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
