package com.podcuaderno.metrics.domain.model;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;

/** Something a user did, reduced to its type and character count: never the content (02 §7). */
public record UsageEvent(UsageEventId id, UserId userId, UsageEventType type, int characters, Instant occurredAt) {

    public UsageEvent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
        if (characters < 0) {
            throw new IllegalArgumentException("characters must not be negative");
        }
    }

    public static UsageEvent occurred(UserId userId, UsageEventType type, int characters, Instant occurredAt) {
        return new UsageEvent(UsageEventId.newId(), userId, type, characters, occurredAt);
    }
}
