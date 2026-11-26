package com.podcuaderno.identity.application.port.in;

/** Asks for a new verification link. The outcome is the same whether or not the email belongs to a pending account. */
public interface ResendEmailVerificationUseCase {

    void resend(ResendEmailVerificationCommand command);
}
