package com.podcuaderno.identity.application.port.in;

/** Issues a verification link and emails it, if the account is still pending and none was sent recently. */
public interface SendEmailVerificationUseCase {

    void send(SendEmailVerificationCommand command);
}
