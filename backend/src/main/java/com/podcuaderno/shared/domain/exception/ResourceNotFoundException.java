package com.podcuaderno.shared.domain.exception;

/** Mapped to 404 /errors/not-found. */
public abstract class ResourceNotFoundException extends DomainException {

    protected ResourceNotFoundException(String message) {
        super(message);
    }
}
