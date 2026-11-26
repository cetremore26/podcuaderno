package com.podcuaderno.identity.application.port.in;

/** Activates the account behind a verification link. Confirming an already used link of an active account is a no-op. */
public interface VerifyEmailUseCase {

    void verify(VerifyEmailCommand command);
}
