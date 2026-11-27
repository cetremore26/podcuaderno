package com.podcuaderno.importing.domain.model;

import java.util.List;
import java.util.Objects;

/** A proposed topic with its concepts in order; it may have none. */
public record DetectedTopic(String title, List<DetectedConcept> concepts) {

    public DetectedTopic {
        Objects.requireNonNull(title, "title must not be null");
        concepts = List.copyOf(Objects.requireNonNull(concepts, "concepts must not be null"));
    }
}
