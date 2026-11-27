package com.podcuaderno.importing.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=too-many-concepts}. */
public class TooManyConceptsException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "too-many-concepts";

    public TooManyConceptsException() {
        super(ERROR_CODE, "The text splits into more than 500 topics or 5000 concepts.");
    }
}
