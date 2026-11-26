package com.podcuaderno.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.application.port.in.RegisterUserCommand;
import com.podcuaderno.identity.domain.event.UserRegistered;
import com.podcuaderno.identity.domain.exception.InvalidEmailAddressException;
import com.podcuaderno.identity.domain.exception.InvalidPasswordException;
import com.podcuaderno.identity.domain.exception.OutdatedPrivacyPolicyException;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.identity.domain.model.UserStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class RegisterUserServiceTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final String PASSWORD = "contraseña-segura";

    private final InMemoryUsers users = new InMemoryUsers();
    private final InMemoryConsents consents = new InMemoryConsents();
    private final FakePasswordHasher hasher = new FakePasswordHasher();
    private final RecordingEventPublisher events = new RecordingEventPublisher();
    private final RegisterUserService service =
            new RegisterUserService(users, consents, hasher, events, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void registersANewStudentWithConsentAndPublishesTheEvent() {
        service.register(new RegisterUserCommand(" Ana@Correo.com ", PASSWORD, "1.0"));

        User user = users.findByEmail(new EmailAddress("ana@correo.com")).orElseThrow();
        assertThat(user.status()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(user.createdAt()).isEqualTo(NOW);
        assertThat(hasher.matches(PASSWORD, user.passwordHash())).isTrue();
        assertThat(consents.findByUserId(user.id())).singleElement().satisfies(consent -> {
            assertThat(consent.policyVersion()).isEqualTo("1.0");
            assertThat(consent.acceptedAt()).isEqualTo(NOW);
        });
        assertThat(events.events()).containsExactly(new UserRegistered(user.id(), NOW));
    }

    @Test
    void anExistingEmailChangesNothingButStillHashesThePassword() {
        service.register(new RegisterUserCommand("ana@correo.com", PASSWORD, "1.0"));
        int savesBefore = users.saves();

        service.register(new RegisterUserCommand("ANA@correo.com", "otra-contraseña", "1.0"));

        assertThat(users.saves()).isEqualTo(savesBefore);
        assertThat(users.count()).isOne();
        assertThat(consents.all()).hasSize(1);
        assertThat(events.events()).hasSize(1);
        assertThat(hasher.hashes()).isEqualTo(2);
    }

    @Test
    void rejectsAnOutdatedPrivacyPolicy() {
        assertThatThrownBy(() -> service.register(new RegisterUserCommand("ana@correo.com", PASSWORD, "0.9")))
                .isInstanceOf(OutdatedPrivacyPolicyException.class);
        assertThat(users.count()).isZero();
        assertThat(events.events()).isEmpty();
    }

    @Test
    void rejectsAShortPassword() {
        assertThatThrownBy(() -> service.register(new RegisterUserCommand("ana@correo.com", "1234567", "1.0")))
                .isInstanceOf(InvalidPasswordException.class);
        assertThat(users.count()).isZero();
    }

    @Test
    void rejectsAMalformedEmail() {
        assertThatThrownBy(() -> service.register(new RegisterUserCommand("ana", PASSWORD, "1.0")))
                .isInstanceOf(InvalidEmailAddressException.class);
    }

    @Test
    void commandHidesItsContentInToString() {
        assertThat(new RegisterUserCommand("ana@correo.com", PASSWORD, "1.0")).hasToString("RegisterUserCommand[***]");
    }
}
