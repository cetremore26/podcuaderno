package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.AuthenticationFailedException;

/** Missing, unknown, expired, rotated or revoked refresh token: the session must start again (401). */
public class InvalidRefreshTokenException extends AuthenticationFailedException {

    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
