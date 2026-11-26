package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.domain.exception.OutdatedPrivacyPolicyException;
import org.junit.jupiter.api.Test;

class PrivacyPolicyTests {

    @Test
    void acceptsTheCurrentVersion() {
        assertThatCode(() -> PrivacyPolicy.requireCurrent("1.0")).doesNotThrowAnyException();
    }

    @Test
    void rejectsAnyOtherVersion() {
        assertThatThrownBy(() -> PrivacyPolicy.requireCurrent("0.9"))
                .isInstanceOf(OutdatedPrivacyPolicyException.class)
                .extracting("code")
                .isEqualTo("outdated-privacy-policy");
        assertThatThrownBy(() -> PrivacyPolicy.requireCurrent(null)).isInstanceOf(OutdatedPrivacyPolicyException.class);
    }
}
