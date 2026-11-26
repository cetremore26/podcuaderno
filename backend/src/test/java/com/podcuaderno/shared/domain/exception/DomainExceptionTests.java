package com.podcuaderno.shared.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class DomainExceptionTests {

    @Test
    void resourceNotFoundKeepsItsMessageAndIsUnchecked() {
        DomainException exception = new ResourceNotFoundException("notebook not found") {};

        assertThat(exception).isInstanceOf(RuntimeException.class).hasMessage("notebook not found");
    }

    @Test
    void businessRuleViolationKeepsItsMessageAndIsUnchecked() {
        DomainException exception = new BusinessRuleViolationException("invalid position") {};

        assertThat(exception).isInstanceOf(RuntimeException.class).hasMessage("invalid position");
    }

    @Test
    void businessRuleViolationUsesTheDefaultCodeUnlessOneIsGiven() {
        BusinessRuleViolationException withDefault = new BusinessRuleViolationException("invalid position") {};
        BusinessRuleViolationException withCode = new BusinessRuleViolationException("invalid-title", "bad title") {};

        assertThat(withDefault.code()).isEqualTo(BusinessRuleViolationException.DEFAULT_CODE);
        assertThat(withCode.code()).isEqualTo("invalid-title");
        assertThat(withCode).hasMessage("bad title");
    }

    @Test
    void businessRuleViolationRejectsANullCode() {
        assertThatThrownBy(() -> new BusinessRuleViolationException(null, "bad title") {})
                .isInstanceOf(NullPointerException.class)
                .hasMessage("code must not be null");
    }

    @Test
    void authenticationFailedKeepsItsMessageAndIsUnchecked() {
        DomainException exception = new AuthenticationFailedException("invalid credentials") {};

        assertThat(exception).isInstanceOf(RuntimeException.class).hasMessage("invalid credentials");
    }

    @Test
    void rateLimitExceededKeepsHowLongToWait() {
        RateLimitExceededException exception = new RateLimitExceededException("slow down", Duration.ofMinutes(3)) {};

        assertThat(exception.retryAfter()).isEqualTo(Duration.ofMinutes(3));
        assertThat(exception).isInstanceOf(DomainException.class).hasMessage("slow down");
    }

    @Test
    void rateLimitExceededRejectsANullWait() {
        assertThatThrownBy(() -> new RateLimitExceededException("slow down", null) {})
                .isInstanceOf(NullPointerException.class)
                .hasMessage("retryAfter must not be null");
    }
}
