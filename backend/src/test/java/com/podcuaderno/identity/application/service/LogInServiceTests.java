package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.application.port.in.LogInCommand;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.domain.exception.EmailNotVerifiedException;
import com.podcuaderno.identity.domain.exception.InvalidCredentialsException;
import com.podcuaderno.identity.domain.exception.TooManyLoginAttemptsException;
import com.podcuaderno.identity.domain.model.AccessTokenPolicy;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.TokenHash;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class LogInServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final EmailAddress ANA = new EmailAddress("ana@correo.com");
    private static final String PASSWORD = "contraseña-segura";

    private final InMemoryUsers users = new InMemoryUsers();
    private final FakePasswordHasher hasher = new FakePasswordHasher();
    private final FakeLoginAttempts attempts = new FakeLoginAttempts();
    private final InMemoryRefreshTokens refreshTokens = new InMemoryRefreshTokens();
    private final LogInService service = new LogInService(
            users, hasher, attempts, new FakeAccessTokenIssuer(), refreshTokens, Clock.fixed(NOW, ZoneOffset.UTC));

    private User user(boolean active) {
        User user = User.register(UserId.newId(), ANA, hasher.hash(PASSWORD), NOW.minus(Duration.ofDays(1)));
        if (active) {
            user.verifyEmail(NOW.minus(Duration.ofHours(1)));
        }
        users.save(user);
        return user;
    }

    @Test
    void startsASessionForAnActiveAccount() {
        User user = user(true);

        SessionTokens tokens = service.logIn(new LogInCommand("ANA@correo.com", PASSWORD));

        assertThat(tokens.accessToken()).startsWith("access:" + user.id().value() + ":STUDENT");
        assertThat(tokens.accessTokenExpiresAt()).isEqualTo(NOW.plus(AccessTokenPolicy.TIME_TO_LIVE));
        assertThat(tokens.refreshTokenExpiresAt()).isEqualTo(NOW.plus(RefreshToken.TIME_TO_LIVE));
        RefreshToken stored =
                refreshTokens.withHash(TokenHash.of(tokens.refreshToken())).orElseThrow();
        assertThat(stored.userId()).isEqualTo(user.id());
        assertThat(stored.isUsable(NOW)).isTrue();
        assertThat(tokens).hasToString("SessionTokens[***]");
    }

    @Test
    void anUnknownEmailIsRejectedGenericallyAfterComparingAgainstTheDummyHash() {
        assertThatThrownBy(() -> service.logIn(new LogInCommand("nadie@correo.com", PASSWORD)))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(hasher.comparisons()).isOne();
        assertThat(attempts.failuresOf(new EmailAddress("nadie@correo.com"))).isOne();
    }

    @Test
    void aWrongPasswordIsRejectedAndCounted() {
        user(true);

        assertThatThrownBy(() -> service.logIn(new LogInCommand("ana@correo.com", "otra-contraseña")))
                .isInstanceOf(InvalidCredentialsException.class);
        assertThat(attempts.failuresOf(ANA)).isOne();
        assertThat(refreshTokens.all()).isEmpty();
    }

    @Test
    void aPendingAccountWithTheRightPasswordIsToldToVerifyWithoutCountingAFailure() {
        user(false);

        assertThatThrownBy(() -> service.logIn(new LogInCommand("ana@correo.com", PASSWORD)))
                .isInstanceOf(EmailNotVerifiedException.class);
        assertThat(attempts.failuresOf(ANA)).isZero();
    }

    @Test
    void theSixthAttemptAfterFiveFailuresIsBlockedEvenWithTheRightPassword() {
        user(true);
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> service.logIn(new LogInCommand("ana@correo.com", "mala-contraseña")))
                    .isInstanceOf(InvalidCredentialsException.class);
        }

        assertThatThrownBy(() -> service.logIn(new LogInCommand("ana@correo.com", PASSWORD)))
                .isInstanceOfSatisfying(
                        TooManyLoginAttemptsException.class,
                        e -> assertThat(e.retryAfter()).isEqualTo(Duration.ofMinutes(15)));
    }

    @Test
    void aSuccessfulLoginClearsTheFailures() {
        user(true);
        assertThatThrownBy(() -> service.logIn(new LogInCommand("ana@correo.com", "mala-contraseña")))
                .isInstanceOf(InvalidCredentialsException.class);

        service.logIn(new LogInCommand("ana@correo.com", PASSWORD));

        assertThat(attempts.failuresOf(ANA)).isZero();
    }
}
