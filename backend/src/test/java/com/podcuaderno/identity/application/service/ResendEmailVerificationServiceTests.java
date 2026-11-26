package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.identity.application.port.in.ResendEmailVerificationCommand;
import com.podcuaderno.identity.domain.event.EmailVerificationRequested;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ResendEmailVerificationServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    private final InMemoryUsers users = new InMemoryUsers();
    private final RecordingEventPublisher events = new RecordingEventPublisher();
    private final ResendEmailVerificationService service =
            new ResendEmailVerificationService(users, events, Clock.fixed(NOW, ZoneOffset.UTC));

    private User user() {
        User user = User.register(UserId.newId(), new EmailAddress("ana@correo.com"), new PasswordHash("{fake}x"), NOW);
        users.save(user);
        return user;
    }

    @Test
    void requestsANewLinkForAPendingAccount() {
        User user = user();

        service.resend(new ResendEmailVerificationCommand("ANA@correo.com"));

        assertThat(events.events()).containsExactly(new EmailVerificationRequested(user.id(), NOW));
    }

    @Test
    void ignoresAnActiveAccount() {
        user().verifyEmail(NOW);

        service.resend(new ResendEmailVerificationCommand("ana@correo.com"));

        assertThat(events.events()).isEmpty();
    }

    @Test
    void ignoresAnUnknownEmail() {
        service.resend(new ResendEmailVerificationCommand("nadie@correo.com"));

        assertThat(events.events()).isEmpty();
    }

    @Test
    void commandHidesTheEmail() {
        assertThat(new ResendEmailVerificationCommand("ana@correo.com"))
                .hasToString("ResendEmailVerificationCommand[***]");
    }
}
