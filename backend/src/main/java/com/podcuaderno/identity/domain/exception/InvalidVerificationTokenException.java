package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=invalid-verification-token}. */
public class InvalidVerificationTokenException extends BusinessRuleViolationException {

    public static final String CODE = "invalid-verification-token";

    public InvalidVerificationTokenException() {
        super(CODE, "The verification link is not valid or has expired.");
    }
}
