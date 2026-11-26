package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=invalid-password}. */
public class InvalidPasswordException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "invalid-password";

    public InvalidPasswordException() {
        super(ERROR_CODE, "The password must have between 8 and 128 characters.");
    }
}
