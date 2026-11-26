package com.podcuaderno.identity.domain.model;

import com.podcuaderno.identity.domain.exception.OutdatedPrivacyPolicyException;

/** Version of the published privacy policy (HU-05). */
public final class PrivacyPolicy {

    /** Must match {@code frontend/src/features/privacy/model}. */
    public static final String CURRENT_VERSION = "1.0";

    private PrivacyPolicy() {}

    public static void requireCurrent(String version) {
        if (!CURRENT_VERSION.equals(version)) {
            throw new OutdatedPrivacyPolicyException();
        }
    }
}
