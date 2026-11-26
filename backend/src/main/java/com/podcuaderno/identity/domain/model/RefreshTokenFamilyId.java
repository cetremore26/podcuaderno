package com.podcuaderno.identity.domain.model;

import java.util.Objects;
import java.util.UUID;

public record RefreshTokenFamilyId(UUID value) {

    public RefreshTokenFamilyId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static RefreshTokenFamilyId newId() {
        return new RefreshTokenFamilyId(UUID.randomUUID());
    }
}
