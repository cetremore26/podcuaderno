package com.podcuaderno.importing.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=unreadable-file}. */
public class UnreadableDocumentException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "unreadable-file";

    public UnreadableDocumentException() {
        super(ERROR_CODE, "The file is not readable text.");
    }
}
