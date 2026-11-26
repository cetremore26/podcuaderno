package com.podcuaderno.notebooks.domain.model;

import java.util.List;
import java.util.Objects;

/** A topic of a notebook with its ordered concepts. Editing rules arrive with HU-11. */
public record Topic(TopicId id, int position, Title title, List<Concept> concepts) {

    public Topic {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        concepts = List.copyOf(Objects.requireNonNull(concepts, "concepts must not be null"));
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
    }
}
