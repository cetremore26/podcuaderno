package com.podcuaderno.shared.domain.exception;

/** Mapped to 422 /errors/business-rule. */
public abstract class BusinessRuleViolationException extends DomainException {

    protected BusinessRuleViolationException(String message) {
        super(message);
    }
}
