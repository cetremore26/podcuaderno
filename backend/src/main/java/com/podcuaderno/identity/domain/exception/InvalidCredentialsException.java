package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.AuthenticationFailedException;

/** Unknown email or wrong password: both answer the same generic 401 (ADR-0009). */
public class InvalidCredentialsException extends AuthenticationFailedException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
