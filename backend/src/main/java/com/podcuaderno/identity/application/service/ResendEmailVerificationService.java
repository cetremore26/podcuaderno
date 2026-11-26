package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.in.ResendEmailVerificationCommand;
import com.podcuaderno.identity.application.port.in.ResendEmailVerificationUseCase;
import com.podcuaderno.identity.application.port.out.UserRepository;
import com.podcuaderno.identity.domain.event.EmailVerificationRequested;
import com.podcuaderno.identity.domain.model.EmailAddress;
import com.podcuaderno.identity.domain.model.User;
import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ResendEmailVerificationService implements ResendEmailVerificationUseCase {

    private final UserRepository users;
    private final DomainEventPublisher events;
    private final Clock clock;

    ResendEmailVerificationService(UserRepository users, DomainEventPublisher events, Clock clock) {
        this.users = users;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public void resend(ResendEmailVerificationCommand command) {
        // The email is sent asynchronously, so the response time does not reveal whether the account exists.
        users.findByEmail(new EmailAddress(command.email()))
                .filter(User::isPendingVerification)
                .ifPresent(user -> events.publish(new EmailVerificationRequested(user.id(), clock.instant())));
    }
}
