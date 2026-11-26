package com.podcuaderno.identity.application.port.in;

/** HU-01: creates a pending account with its consent. The outcome is the same whether or not the email exists. */
public interface RegisterUserUseCase {

    void register(RegisterUserCommand command);
}
