package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.application.port.in.VerifyEmailCommand;
import com.podcuaderno.identity.domain.exception.InvalidVerificationTokenException;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.EmailVerificationTokenId;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class VerifyEmailServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private final InMemoryUsers users = new InMemoryUsers();
    private final InMemoryEmailVerificationTokens tokens = new InMemoryEmailVerificationTokens();
    private final OpaqueToken raw = OpaqueToken.generate();

    private VerifyEmailService serviceAt(Instant instant) {
        return new VerifyEmailService(users, tokens, Clock.fixed(instant, ZoneOffset.UTC));
    }

    private User pendingUserWithToken() {
        User user = User.register(
                UserId.newId(), new EmailAddress("ana@correo.com"), new PasswordHash("{fake}x"), NOW);
        users.save(user);
        tokens.save(EmailVerificationToken.issue(EmailVerificationTokenId.newId(), user.id(), raw.hash(), NOW));
        return user;
    }

    @Test
    void activatesTheAccountAndConsumesTheToken() {
        User user = pendingUserWithToken();
        Instant later = NOW.plus(Duration.ofHours(1));

        serviceAt(later).verify(new VerifyEmailCommand(raw.value()));

        User verified = users.findById(user.id()).orElseThrow();
        assertThat(verified.isActive()).isTrue();
        assertThat(verified.emailVerifiedAt()).contains(later);
        assertThat(tokens.findByHash(raw.hash()).orElseThrow().isUsed()).isTrue();
    }

    @Test
    void confirmingTheSameLinkTwiceDoesNotFail() {
        pendingUserWithToken();
        serviceAt(NOW.plusSeconds(10)).verify(new VerifyEmailCommand(raw.value()));

        assertThatCode(() -> serviceAt(NOW.plusSeconds(20)).verify(new VerifyEmailCommand(raw.value())))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsAnExpiredToken() {
        User user = pendingUserWithToken();

        assertThatThrownBy(() -> serviceAt(NOW.plus(Duration.ofHours(25))).verify(new VerifyEmailCommand(raw.value())))
                .isInstanceOf(InvalidVerificationTokenException.class);
        assertThat(users.findById(user.id()).orElseThrow().isActive()).isFalse();
    }

    @Test
    void rejectsAnUnknownToken() {
        pendingUserWithToken();

        assertThatThrownBy(() -> serviceAt(NOW).verify(new VerifyEmailCommand("invented")))
                .isInstanceOf(InvalidVerificationTokenException.class);
    }

    @Test
    void rejectsATokenWhoseUserNoLongerExists() {
        tokens.save(EmailVerificationToken.issue(EmailVerificationTokenId.newId(), UserId.newId(), raw.hash(), NOW));

        assertThatThrownBy(() -> serviceAt(NOW).verify(new VerifyEmailCommand(raw.value())))
                .isInstanceOf(InvalidVerificationTokenException.class);
    }

    @Test
    void commandHidesTheToken() {
        assertThat(new VerifyEmailCommand("secret")).hasToString("VerifyEmailCommand[***]");
    }
}
