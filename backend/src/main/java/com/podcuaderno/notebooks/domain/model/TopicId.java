package com.podcuaderno.notebooks.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TopicId(UUID value) {

    public TopicId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static TopicId newId() {
        return new TopicId(UUID.randomUUID());
    }
}
