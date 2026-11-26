package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=invalid-verification-token}. */
public class InvalidVerificationTokenException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "invalid-verification-token";

    public InvalidVerificationTokenException() {
        super(ERROR_CODE, "The verification link is not valid or has expired.");
    }
}
