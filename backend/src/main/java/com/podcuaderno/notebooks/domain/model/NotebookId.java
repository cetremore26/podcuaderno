package com.podcuaderno.notebooks.domain.model;

import java.util.Objects;
import java.util.UUID;

public record NotebookId(UUID value) {

    public NotebookId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static NotebookId newId() {
        return new NotebookId(UUID.randomUUID());
    }
}
