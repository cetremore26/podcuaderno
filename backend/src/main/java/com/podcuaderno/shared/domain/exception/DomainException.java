package com.podcuaderno.shared.domain.exception;

/** Base type of every exception with business meaning. */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
