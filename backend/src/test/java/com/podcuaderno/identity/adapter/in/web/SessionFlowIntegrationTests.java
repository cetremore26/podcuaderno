package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.podcuaderno.IntegrationTest;
import com.podcuaderno.RecordingEmailSender;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@IntegrationTest
class SessionFlowIntegrationTests {

    private static final String PASSWORD = "contraseña-segura";

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private RecordingEmailSender emails;

    private MvcTestResult post(String uri, String body) {
        return mvc.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }

    /** Registers, verifies and logs in a new student; returns the refresh cookie value. */
    private String loggedInRefreshToken() {
        String email = UUID.randomUUID() + "@correo.com";
        post("/api/v1/auth/registrations", """
                {"email": "%s", "password": "%s", "privacyPolicyAccepted": true, "privacyPolicyVersion": "1.0"}
                """.formatted(email, PASSWORD));
        await().atMost(Duration.ofSeconds(10))
                .until(() -> emails.lastTokenFor(email).isPresent());
        post(
                "/api/v1/auth/email-verifications/confirmations",
                "{\"token\": \"%s\"}".formatted(emails.lastTokenFor(email).orElseThrow()));
        MvcTestResult login =
                post("/api/v1/auth/login", "{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, PASSWORD));
        assertThat(login).hasStatusOk();
        return refreshCookieOf(login);
    }

    private static String refreshCookieOf(MvcTestResult result) {
        Cookie cookie = result.getResponse().getCookie(RefreshTokenCookies.NAME);
        assertThat(cookie).isNotNull();
        return cookie.getValue();
    }

    private MvcTestResult refresh(String refreshToken) {
        return mvc.post()
                .uri("/api/v1/auth/refresh")
                .cookie(new Cookie(RefreshTokenCookies.NAME, refreshToken))
                .exchange();
    }

    @Test
    void refreshReturnsANewAccessTokenAndADifferentCookie() {
        String first = loggedInRefreshToken();

        MvcTestResult renewed = refresh(first);

        assertThat(renewed).hasStatusOk();
        assertThat(renewed).bodyJson().extractingPath("$.accessToken").isNotNull();
        assertThat(refreshCookieOf(renewed)).isNotEqualTo(first);
    }

    @Test
    void reusingAnOldCookieClosesTheWholeSession() {
        String first = loggedInRefreshToken();
        String second = refreshCookieOf(refresh(first));

        assertThat(refresh(first)).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(refresh(second)).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void logoutDeletesTheCookieAndInvalidatesItOnTheServer() {
        String refreshToken = loggedInRefreshToken();

        MvcTestResult logout = mvc.post()
                .uri("/api/v1/auth/logout")
                .cookie(new Cookie(RefreshTokenCookies.NAME, refreshToken))
                .exchange();

        assertThat(logout).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(logout.getResponse().getCookie(RefreshTokenCookies.NAME))
                .satisfies(cookie -> assertThat(cookie.getMaxAge()).isZero());
        assertThat(refresh(refreshToken)).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refreshWithoutCookieAnswersAnUnauthenticatedProblem() {
        assertThat(mvc.post().uri("/api/v1/auth/refresh"))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.type")
                .isEqualTo("/errors/unauthenticated");
    }
}
