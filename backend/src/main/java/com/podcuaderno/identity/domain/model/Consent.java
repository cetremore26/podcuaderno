package com.podcuaderno.identity.domain.model;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;

/** Acceptance of a privacy policy version, kept as evidence (HU-05, Ley 1581 de 2012). */
public record Consent(ConsentId id, UserId userId, String policyVersion, Instant acceptedAt) {

    public Consent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(policyVersion, "policyVersion must not be null");
        Objects.requireNonNull(acceptedAt, "acceptedAt must not be null");
    }

    public static Consent accept(ConsentId id, UserId userId, String policyVersion, Instant now) {
        return new Consent(id, userId, policyVersion, now);
    }
}
