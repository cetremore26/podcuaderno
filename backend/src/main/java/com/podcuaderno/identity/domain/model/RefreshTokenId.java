package com.podcuaderno.identity.domain.model;

import java.util.Objects;
import java.util.UUID;

public record RefreshTokenId(UUID value) {

    public RefreshTokenId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static RefreshTokenId newId() {
        return new RefreshTokenId(UUID.randomUUID());
    }
}
