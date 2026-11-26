package com.podcuaderno.identity.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.podcuaderno.identity.application.port.in.LogInCommand;
import com.podcuaderno.identity.application.port.in.LogInUseCase;
import com.podcuaderno.identity.application.port.in.LogOutCommand;
import com.podcuaderno.identity.application.port.in.LogOutUseCase;
import com.podcuaderno.identity.application.port.in.RefreshSessionCommand;
import com.podcuaderno.identity.application.port.in.RefreshSessionUseCase;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.domain.exception.EmailNotVerifiedException;
import com.podcuaderno.identity.domain.exception.InvalidCredentialsException;
import com.podcuaderno.identity.domain.exception.InvalidRefreshTokenException;
import com.podcuaderno.identity.domain.exception.TooManyLoginAttemptsException;
import com.podcuaderno.shared.adapter.in.web.ApiSecurityTestConfiguration;
import jakarta.servlet.http.Cookie;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@WebMvcTest(SessionController.class)
@Import({ApiSecurityTestConfiguration.class, RefreshTokenCookies.class, SessionControllerTests.FixedClock.class})
class SessionControllerTests {

    static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final String BODY = "{\"email\": \"ana@correo.com\", \"password\": \"contraseña-segura\"}";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private LogInUseCase logIn;

    @MockitoBean
    private RefreshSessionUseCase refreshSession;

    @MockitoBean
    private LogOutUseCase logOut;

    private MvcTestResult logIn() {
        return mvc.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY)
                .exchange();
    }

    @Test
    void returnsTheAccessTokenAndSetsAHardenedRefreshCookie() {
        when(logIn.logIn(new LogInCommand("ana@correo.com", "contraseña-segura")))
                .thenReturn(new SessionTokens(
                        "access-token",
                        NOW.plus(Duration.ofMinutes(15)),
                        "refresh-token",
                        NOW.plus(Duration.ofDays(14))));

        MvcTestResult result = logIn();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.accessToken").isEqualTo("access-token");
        assertThat(result).bodyJson().extractingPath("$.tokenType").isEqualTo("Bearer");
        assertThat(result).bodyJson().extractingPath("$.expiresIn").isEqualTo(900);
        assertThat(result).bodyText().doesNotContain("refresh-token");
        String cookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(cookie)
                .startsWith("pc_refresh=refresh-token")
                .contains("HttpOnly", "Secure", "SameSite=Strict", "Path=/api/v1/auth", "Max-Age=1209600");
    }

    @Test
    void invalidCredentialsAnswerAGeneric401() {
        when(logIn.logIn(any())).thenThrow(new InvalidCredentialsException());

        MvcTestResult result = logIn();

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(result).bodyJson().extractingPath("$.type").isEqualTo("/errors/unauthenticated");
        assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE)).isNull();
    }

    @Test
    void aPendingAccountAnswers422WithItsCode() {
        when(logIn.logIn(any())).thenThrow(new EmailNotVerifiedException());

        MvcTestResult result = logIn();

        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(result).bodyJson().extractingPath("$.code").isEqualTo("email-not-verified");
    }

    @Test
    void tooManyAttemptsAnswer429WithRetryAfter() {
        when(logIn.logIn(any())).thenThrow(new TooManyLoginAttemptsException(Duration.ofMinutes(15)));

        MvcTestResult result = logIn();

        assertThat(result).hasStatus(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(result).hasHeader(HttpHeaders.RETRY_AFTER, "900");
        assertThat(result).bodyJson().extractingPath("$.type").isEqualTo("/errors/rate-limit");
    }

    @Test
    void rejectsAMalformedBody() {
        MvcTestResult result = mvc.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"ana\"}")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void refreshRotatesTheCookie() {
        when(refreshSession.refresh(new RefreshSessionCommand("old-refresh")))
                .thenReturn(new SessionTokens(
                        "new-access", NOW.plus(Duration.ofMinutes(15)), "new-refresh", NOW.plus(Duration.ofDays(14))));

        MvcTestResult result = mvc.post()
                .uri("/api/v1/auth/refresh")
                .cookie(new Cookie(RefreshTokenCookies.NAME, "old-refresh"))
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.accessToken").isEqualTo("new-access");
        assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .startsWith("pc_refresh=new-refresh")
                .contains("HttpOnly", "SameSite=Strict");
    }

    @Test
    void refreshWithoutCookieAnswers401() {
        assertThat(mvc.post().uri("/api/v1/auth/refresh"))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.type")
                .isEqualTo("/errors/unauthenticated");
    }

    @Test
    void refreshWithAnInvalidCookieAnswers401() {
        when(refreshSession.refresh(any())).thenThrow(new InvalidRefreshTokenException());

        assertThat(mvc.post().uri("/api/v1/auth/refresh").cookie(new Cookie(RefreshTokenCookies.NAME, "stolen")))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void logoutRevokesTheSessionAndDeletesTheCookie() {
        MvcTestResult result = mvc.post()
                .uri("/api/v1/auth/logout")
                .cookie(new Cookie(RefreshTokenCookies.NAME, "current-refresh"))
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .startsWith("pc_refresh=;")
                .contains("Max-Age=0", "Path=/api/v1/auth", "HttpOnly");
        verify(logOut).logOut(new LogOutCommand("current-refresh"));
    }

    @Test
    void logoutWithoutCookieStillAnswers204() {
        assertThat(mvc.post().uri("/api/v1/auth/logout")).hasStatus(HttpStatus.NO_CONTENT);
        verify(logOut, never()).logOut(any());
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class FixedClock {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneOffset.UTC);
        }
    }
}
