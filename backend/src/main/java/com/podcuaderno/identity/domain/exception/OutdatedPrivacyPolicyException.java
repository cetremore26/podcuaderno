package com.podcuaderno.identity.domain.exception;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;

/** Mapped to 422 with {@code code=outdated-privacy-policy}. */
public class OutdatedPrivacyPolicyException extends BusinessRuleViolationException {

    public static final String ERROR_CODE = "outdated-privacy-policy";

    public OutdatedPrivacyPolicyException() {
        super(ERROR_CODE, "The accepted privacy policy version is not the current one.");
    }
}
