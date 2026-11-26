package com.podcuaderno.identity.domain.model;

import java.util.Objects;
import java.util.UUID;

public record ConsentId(UUID value) {

    public ConsentId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static ConsentId newId() {
        return new ConsentId(UUID.randomUUID());
    }
}
