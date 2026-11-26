package com.podcuaderno.identity.adapter.in.event;

import com.podcuaderno.identity.application.port.in.SendEmailVerificationCommand;
import com.podcuaderno.identity.application.port.in.SendEmailVerificationUseCase;
import com.podcuaderno.identity.domain.event.EmailVerificationRequested;
import com.podcuaderno.identity.domain.event.UserRegistered;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Sends the verification email after the registration commits, asynchronously, so the response never waits for SMTP
 * (ADR-0010).
 */
@Component
class EmailVerificationListener {

    private final SendEmailVerificationUseCase sendEmailVerification;

    EmailVerificationListener(SendEmailVerificationUseCase sendEmailVerification) {
        this.sendEmailVerification = sendEmailVerification;
    }

    @ApplicationModuleListener
    void on(UserRegistered event) {
        sendEmailVerification.send(new SendEmailVerificationCommand(event.userId()));
    }

    @ApplicationModuleListener
    void on(EmailVerificationRequested event) {
        sendEmailVerification.send(new SendEmailVerificationCommand(event.userId()));
    }
}
