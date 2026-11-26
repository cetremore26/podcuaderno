package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.RegisterUserCommand;
import com.podcuaderno.identity.application.port.in.RegisterUserUseCase;
import com.podcuaderno.identity.application.port.out.ConsentRepository;
import com.podcuaderno.identity.application.port.out.PasswordHasher;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.event.UserRegistered;
import com.podcuaderno.identity.domain.model.Consent;
import com.podcuaderno.identity.domain.model.ConsentId;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.PasswordHash;
import com.podcuaderno.identity.domain.model.PasswordPolicy;
import com.podcuaderno.identity.domain.model.PrivacyPolicy;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository users;
    private final ConsentRepository consents;
    private final PasswordHasher hasher;
    private final DomainEventPublisher events;
    private final Clock clock;

    RegisterUserService(
            UserRepository users,
            ConsentRepository consents,
            PasswordHasher hasher,
            DomainEventPublisher events,
            Clock clock) {
        this.users = users;
        this.consents = consents;
        this.hasher = hasher;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public void register(RegisterUserCommand command) {
        PrivacyPolicy.requireCurrent(command.privacyPolicyVersion());
        EmailAddress email = new EmailAddress(command.email());
        PasswordPolicy.validate(command.password());
        PasswordHash hash = hasher.hash(command.password()); // always hash: same cost whether or not the email exists
        if (users.existsByEmail(email)) {
            return; // generic outcome: never reveal that the email is registered (HU-01)
        }
        Instant now = clock.instant();
        User user = User.register(UserId.newId(), email, hash, now);
        users.save(user);
        consents.save(Consent.accept(ConsentId.newId(), user.id(), PrivacyPolicy.CURRENT_VERSION, now));
        events.publish(new UserRegistered(user.id(), now));
    }
}
