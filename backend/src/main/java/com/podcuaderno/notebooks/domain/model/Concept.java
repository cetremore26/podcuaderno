package com.podcuaderno.notebooks.domain.model;

import java.util.Objects;

/** A concept of a topic. Editing rules arrive with HU-11. */
public record Concept(ConceptId id, int position, Title title, String text) {

    public Concept {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(text, "text must not be null");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
    }
}
