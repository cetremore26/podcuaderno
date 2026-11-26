package com.podcuaderno.identity.application.port.in;

/** HU-02: invalidates the session on the server. Always succeeds. */
public interface LogOutUseCase {

    void logOut(LogOutCommand command);
}
