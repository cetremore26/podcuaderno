package com.podcuaderno.importing.domain.model;

import com.podcuaderno.importing.domain.exception.DocumentTooLongException;
import com.podcuaderno.importing.domain.exception.TooManyConceptsException;

/**
 * Limits of an import (RNF-11, ADR-0013). They must match {@code NotebookLimits} of notebooks and
 * {@code frontend/src/features/import/model/limits.ts}: modules do not know each other, so the values are repeated
 * on purpose.
 */
public final class ImportLimits {

    /** RNF-11: the sum of the concept texts of a notebook. */
    public static final int MAX_CHARACTERS = 200_000;
    /** Markup and blank lines are removed while reading; beyond this the source is rejected before parsing. */
    public static final int MAX_SOURCE_CHARACTERS = 2 * MAX_CHARACTERS;

    public static final int MAX_TOPICS = 500;
    public static final int MAX_CONCEPTS = 5_000;
    public static final int MAX_TITLE_LENGTH = 150;

    private ImportLimits() {}

    /** Rejects a source text that is too long to be worth parsing (R-09). */
    public static void requireReadableLength(int sourceCharacters) {
        if (sourceCharacters > MAX_SOURCE_CHARACTERS) {
            throw new DocumentTooLongException();
        }
    }

    static void requireWithin(DetectedStructure structure) {
        if (structure.characterCount() > MAX_CHARACTERS) {
            throw new DocumentTooLongException();
        }
        if (structure.topicCount() > MAX_TOPICS || structure.conceptCount() > MAX_CONCEPTS) {
            throw new TooManyConceptsException();
        }
    }
}
