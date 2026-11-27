package com.podcuaderno.importing.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=text-too-long}. */
public class DocumentTooLongException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "text-too-long";

    public DocumentTooLongException() {
        super(ERROR_CODE, "The text exceeds the limit of 200000 characters.");
    }
}
