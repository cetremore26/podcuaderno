package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.application.port.in.RefreshSessionCommand;
import com.podcuaderno.identity.application.port.in.SessionTokens;
import com.podcuaderno.identity.domain.exception.InvalidRefreshTokenException;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class RefreshSessionServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private final InMemoryRefreshTokens refreshTokens = new InMemoryRefreshTokens();
    private final InMemoryUsers users = new InMemoryUsers();
    private final OpaqueToken raw = OpaqueToken.generate();

    private RefreshSessionService serviceAt(Instant instant) {
        return new RefreshSessionService(
                refreshTokens, users, new FakeAccessTokenIssuer(), Clock.fixed(instant, ZoneOffset.UTC));
    }

    private User sessionOf(boolean active) {
        User user = User.register(
                UserId.newId(), new EmailAddress("ana@correo.com"), new PasswordHash("{fake}x"), NOW.minusSeconds(60));
        if (active) {
            user.verifyEmail(NOW.minusSeconds(30));
        }
        users.save(user);
        refreshTokens.save(RefreshToken.issue(user.id(), raw.hash(), NOW));
        return user;
    }

    @Test
    void rotatesTheRefreshTokenAndIssuesANewAccessToken() {
        User user = sessionOf(true);
        Instant later = NOW.plus(Duration.ofMinutes(20));

        SessionTokens tokens = serviceAt(later).refresh(new RefreshSessionCommand(raw.value()));

        assertThat(tokens.accessToken()).contains(user.id().value().toString());
        assertThat(tokens.refreshToken()).isNotEqualTo(raw.value());
        assertThat(tokens.refreshTokenExpiresAt()).isEqualTo(later.plus(RefreshToken.TIME_TO_LIVE));
        RefreshToken old = refreshTokens.withHash(raw.hash()).orElseThrow();
        RefreshToken next = refreshTokens
                .withHash(OpaqueToken.of(tokens.refreshToken()).hash())
                .orElseThrow();
        assertThat(old.wasRotated()).isTrue();
        assertThat(next.familyId()).isEqualTo(old.familyId());
        assertThat(next.isUsable(later)).isTrue();
    }

    @Test
    void reusingARotatedTokenRevokesTheWholeFamily() {
        sessionOf(true);
        SessionTokens renewed = serviceAt(NOW.plusSeconds(10)).refresh(new RefreshSessionCommand(raw.value()));

        assertThatThrownBy(() -> serviceAt(NOW.plusSeconds(20)).refresh(new RefreshSessionCommand(raw.value())))
                .isInstanceOf(InvalidRefreshTokenException.class);

        assertThat(refreshTokens.all())
                .allSatisfy(token -> assertThat(token.revokedAt()).contains(NOW.plusSeconds(20)));
        assertThatThrownBy(
                        () -> serviceAt(NOW.plusSeconds(30)).refresh(new RefreshSessionCommand(renewed.refreshToken())))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rejectsAnUnknownToken() {
        assertThatThrownBy(() -> serviceAt(NOW).refresh(new RefreshSessionCommand("unknown")))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rejectsAnExpiredToken() {
        sessionOf(true);

        assertThatThrownBy(
                        () -> serviceAt(NOW.plus(Duration.ofDays(15))).refresh(new RefreshSessionCommand(raw.value())))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void rejectsATokenOfAnAccountThatIsNoLongerActive() {
        sessionOf(false);

        assertThatThrownBy(() -> serviceAt(NOW).refresh(new RefreshSessionCommand(raw.value())))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void commandHidesTheToken() {
        assertThat(new RefreshSessionCommand("secret")).hasToString("RefreshSessionCommand[***]");
    }
}
