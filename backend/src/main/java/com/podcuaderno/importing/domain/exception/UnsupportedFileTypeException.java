package com.podcuaderno.importing.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=unsupported-file-type}. */
public class UnsupportedFileTypeException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "unsupported-file-type";

    public UnsupportedFileTypeException() {
        super(ERROR_CODE, "Only .txt, .md and .markdown files can be imported.");
    }
}
