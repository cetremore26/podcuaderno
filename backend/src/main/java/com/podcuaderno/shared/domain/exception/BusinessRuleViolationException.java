package com.podcuaderno.shared.domain.exception;

import java.util.Objects;

/**
 * Mapped to 422 /errors/business-rule. The {@link #code()} lets clients tell rules apart without parsing the message
 * (docs/05 §5.6).
 */
public abstract class BusinessRuleViolationException extends DomainException {

    public static final String DEFAULT_CODE = "business-rule";

    private final String code;

    protected BusinessRuleViolationException(String message) {
        this(DEFAULT_CODE, message);
    }

    protected BusinessRuleViolationException(String code, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code must not be null");
    }

    public String code() {
        return code;
    }
}
