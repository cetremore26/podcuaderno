package com.podcuaderno.importing.domain.model;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.importing.domain.exception.DocumentTooLongException;
import com.podcuaderno.importing.domain.exception.TooManyConceptsException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ImportLimitsTests {

    @Test
    void theSourceMayHaveTwiceTheCharactersOfANotebook() {
        assertThatCode(() -> ImportLimits.requireReadableLength(400_000)).doesNotThrowAnyException();
        assertThatThrownBy(() -> ImportLimits.requireReadableLength(400_001))
                .isInstanceOf(DocumentTooLongException.class);
    }

    @Test
    void aStructureWithinTheLimitsPasses() {
        assertThatCode(() -> ImportLimits.requireWithin(structure(500, 10, "a".repeat(40))))
                .doesNotThrowAnyException();
    }

    @Test
    void tooManyCharactersAreRejected() {
        assertThatThrownBy(() -> ImportLimits.requireWithin(structure(1, 1, "a".repeat(200_001))))
                .isInstanceOf(DocumentTooLongException.class);
    }

    @Test
    void tooManyTopicsOrConceptsAreRejected() {
        assertThatThrownBy(() -> ImportLimits.requireWithin(structure(501, 0, "")))
                .isInstanceOf(TooManyConceptsException.class);
        assertThatThrownBy(() -> ImportLimits.requireWithin(structure(1, 5_001, "")))
                .isInstanceOf(TooManyConceptsException.class);
    }

    private static DetectedStructure structure(int topics, int conceptsPerTopic, String text) {
        List<DetectedConcept> concepts = Collections.nCopies(conceptsPerTopic, new DetectedConcept("Concepto", text));
        return new DetectedStructure(
                "Título",
                StructureSource.HEADINGS,
                Collections.nCopies(topics, new DetectedTopic("Tema", concepts)),
                List.of());
    }
}
