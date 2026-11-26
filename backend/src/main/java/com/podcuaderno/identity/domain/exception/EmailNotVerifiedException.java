package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=email-not-verified}; only raised after the password matched. */
public class EmailNotVerifiedException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "email-not-verified";

    public EmailNotVerifiedException() {
        super(ERROR_CODE, "The email address has not been verified yet.");
    }
}
