package com.podcuaderno.identity.domain.event;

import com.podcuaderno.shared.domain.DomainEvent;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;

/** A pending user asked for a new verification link (HU-01). */
public record EmailVerificationRequested(UserId userId, Instant occurredAt) implements DomainEvent {

    public EmailVerificationRequested {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
