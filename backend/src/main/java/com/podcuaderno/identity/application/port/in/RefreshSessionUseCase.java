package com.podcuaderno.identity.application.port.in;

/** HU-02: renews the access token and rotates the refresh token. */
public interface RefreshSessionUseCase {

    SessionTokens refresh(RefreshSessionCommand command);
}
