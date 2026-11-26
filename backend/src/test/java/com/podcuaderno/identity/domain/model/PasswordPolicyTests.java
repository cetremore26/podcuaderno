package com.podcuaderno.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.identity.domain.exception.InvalidPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordPolicyTests {

    @ParameterizedTest
    @ValueSource(ints = {8, 128})
    void acceptsTheLimitsCountingCharactersNotBytes(int length) {
        assertThatCode(() -> PasswordPolicy.validate("ñ".repeat(length))).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 7, 129})
    void rejectsLengthsOutsideTheLimits(int length) {
        assertThatThrownBy(() -> PasswordPolicy.validate("a".repeat(length)))
                .isInstanceOf(InvalidPasswordException.class)
                .extracting("code")
                .isEqualTo("invalid-password");
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> PasswordPolicy.validate(null)).isInstanceOf(InvalidPasswordException.class);
    }
}
