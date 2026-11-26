package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.podcuaderno.identity.application.port.in.LogOutCommand;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.RefreshToken;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class LogOutServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private final InMemoryRefreshTokens refreshTokens = new InMemoryRefreshTokens();
    private final LogOutService service = new LogOutService(refreshTokens, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void revokesTheFamilyOfThePresentedToken() {
        OpaqueToken raw = OpaqueToken.generate();
        RefreshToken current = RefreshToken.issue(UserId.newId(), raw.hash(), NOW.minusSeconds(60));
        RefreshToken next = current.rotate(OpaqueToken.generate().hash(), NOW.minusSeconds(30));
        RefreshToken otherSession =
                RefreshToken.issue(current.userId(), OpaqueToken.generate().hash(), NOW);
        refreshTokens.save(current);
        refreshTokens.save(next);
        refreshTokens.save(otherSession);

        service.logOut(new LogOutCommand(raw.value()));

        assertThat(refreshTokens.withHash(next.tokenHash()).orElseThrow().revokedAt())
                .contains(NOW);
        assertThat(refreshTokens
                        .withHash(otherSession.tokenHash())
                        .orElseThrow()
                        .isUsable(NOW))
                .isTrue();
    }

    @Test
    void anUnknownTokenIsIgnored() {
        assertThatCode(() -> service.logOut(new LogOutCommand("unknown"))).doesNotThrowAnyException();
    }

    @Test
    void commandHidesTheToken() {
        assertThat(new LogOutCommand("secret")).hasToString("LogOutCommand[***]");
    }
}
