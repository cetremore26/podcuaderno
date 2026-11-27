package com.podcuaderno.importing.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=empty-document}. */
public class EmptyDocumentException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "empty-document";

    public EmptyDocumentException() {
        super(ERROR_CODE, "The document has no text.");
    }
}
