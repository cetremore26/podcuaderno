package com.podcuaderno.shared.domain.exception;

/** Mapped to 401 /errors/unauthenticated with a generic detail: credentials or session are not valid. */
public abstract class AuthenticationFailedException extends DomainException {

    protected AuthenticationFailedException(String message) {
        super(message);
    }
}
