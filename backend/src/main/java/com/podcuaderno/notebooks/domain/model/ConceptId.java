package com.podcuaderno.notebooks.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ConceptId(UUID value) {

    public ConceptId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ConceptId newId() {
        return new ConceptId(UUID.randomUUID());
    }
}
