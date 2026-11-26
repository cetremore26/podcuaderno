package com.podcuaderno.identity.application.port.in;

/** HU-02: starts a session for an active account. */
public interface LogInUseCase {

    SessionTokens logIn(LogInCommand command);
}
