package com.podcuaderno.shared.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

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
}
