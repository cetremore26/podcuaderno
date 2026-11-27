package com.podcuaderno.importing.domain.model;

import java.util.Objects;

/** How many elements of one kind were left out of the document. */
public record OmittedElement(OmittedElementKind kind, int count) {

    public OmittedElement {
        Objects.requireNonNull(kind, "kind must not be null");
        if (count < 1) {
            throw new IllegalArgumentException("count must be positive");
        }
    }
}
