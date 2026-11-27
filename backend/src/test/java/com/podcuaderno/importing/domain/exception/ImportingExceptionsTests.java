package com.podcuaderno.importing.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.shared.domain.exception.BusinessRuleViolationException;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ImportingExceptionsTests {

    static Stream<Arguments> exceptions() {
        return Stream.of(
                Arguments.of(
                        (Supplier<BusinessRuleViolationException>) UnsupportedFileTypeException::new,
                        "unsupported-file-type"),
                Arguments.of(
                        (Supplier<BusinessRuleViolationException>) UnreadableDocumentException::new, "unreadable-file"),
                Arguments.of((Supplier<BusinessRuleViolationException>) EmptyDocumentException::new, "empty-document"),
                Arguments.of((Supplier<BusinessRuleViolationException>) DocumentTooLongException::new, "text-too-long"),
                Arguments.of(
                        (Supplier<BusinessRuleViolationException>) TooManyConceptsException::new, "too-many-concepts"));
    }

    @ParameterizedTest
    @MethodSource("exceptions")
    void eachRuleHasItsCodeAndAnEnglishMessage(Supplier<BusinessRuleViolationException> exception, String code) {
        BusinessRuleViolationException created = exception.get();

        assertThat(created.code()).isEqualTo(code);
        assertThat(created.getMessage()).isNotBlank();
    }
}
