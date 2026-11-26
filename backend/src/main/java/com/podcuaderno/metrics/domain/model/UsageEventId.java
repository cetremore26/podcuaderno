package com.podcuaderno.metrics.domain.model;

import java.util.Objects;
import java.util.UUID;

public record UsageEventId(UUID value) {

    public UsageEventId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static UsageEventId newId() {
        return new UsageEventId(UUID.randomUUID());
    }
}
