package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.identity.application.port.in.SendEmailVerificationCommand;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.EmailVerificationToken;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class SendEmailVerificationServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private final InMemoryUsers users = new InMemoryUsers();
    private final InMemoryEmailVerificationTokens tokens = new InMemoryEmailVerificationTokens();
    private final FakeEmailSender emailSender = new FakeEmailSender();

    private SendEmailVerificationService serviceAt(Instant instant) {
        return new SendEmailVerificationService(users, tokens, emailSender, Clock.fixed(instant, ZoneOffset.UTC));
    }

    private User pendingUser() {
        User user = User.register(UserId.newId(), new EmailAddress("ana@correo.com"), new PasswordHash("{fake}x"), NOW);
        users.save(user);
        return user;
    }

    @Test
    void sendsALinkToAPendingUserAndStoresOnlyItsHash() {
        User user = pendingUser();

        serviceAt(NOW).send(new SendEmailVerificationCommand(user.id()));

        assertThat(emailSender.sent()).singleElement().satisfies(email -> {
            assertThat(email.to()).isEqualTo(user.email());
            assertThat(email.expiresAt()).isEqualTo(NOW.plus(EmailVerificationToken.TIME_TO_LIVE));
            assertThat(tokens.findByHash(email.token().hash())).isPresent();
        });
    }

    @Test
    void doesNotSendToAnActiveUser() {
        User user = pendingUser();
        user.verifyEmail(NOW);

        serviceAt(NOW).send(new SendEmailVerificationCommand(user.id()));

        assertThat(emailSender.sent()).isEmpty();
        assertThat(tokens.count()).isZero();
    }

    @Test
    void doesNotSendToAnUnknownUser() {
        serviceAt(NOW).send(new SendEmailVerificationCommand(UserId.newId()));

        assertThat(emailSender.sent()).isEmpty();
    }

    @Test
    void respectsTheResendCooldown() {
        User user = pendingUser();
        serviceAt(NOW).send(new SendEmailVerificationCommand(user.id()));

        serviceAt(NOW.plus(Duration.ofMinutes(1))).send(new SendEmailVerificationCommand(user.id()));
        assertThat(emailSender.sent()).hasSize(1);

        serviceAt(NOW.plus(Duration.ofMinutes(2)).plusSeconds(1)).send(new SendEmailVerificationCommand(user.id()));
        assertThat(emailSender.sent()).hasSize(2);
    }
}
