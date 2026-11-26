package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=invalid-email}. */
public class InvalidEmailAddressException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "invalid-email";

    public InvalidEmailAddressException() {
        super(ERROR_CODE, "The email address is not valid.");
    }
}
