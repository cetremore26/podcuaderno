package com.podcuaderno.notebooks.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=invalid-title}. */
public class InvalidTitleException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "invalid-title";

    public InvalidTitleException() {
        super(ERROR_CODE, "The title must have between 1 and 150 characters.");
    }
}
