package com.podcuaderno.identity.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EmailVerificationTokenId(UUID value) {

    public EmailVerificationTokenId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static EmailVerificationTokenId newId() {
        return new EmailVerificationTokenId(UUID.randomUUID());
    }
}
