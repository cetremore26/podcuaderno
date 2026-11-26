package com.podcuaderno.identity.domain.event;

import com.podcuaderno.shared.domain.DomainEvent;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;

/** A student created an account, still pending verification (HU-01). Carries no personal data. */
public record UserRegistered(UserId userId, Instant occurredAt) implements DomainEvent {

    public UserRegistered {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
