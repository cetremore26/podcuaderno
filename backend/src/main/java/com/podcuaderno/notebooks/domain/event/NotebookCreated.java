package com.podcuaderno.notebooks.domain.event;

import com.podcuaderno.shared.domain.DomainEvent;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A student created a notebook. Carries a plain {@code UUID}, not {@code NotebookId}, so listeners never depend on
 * types internal to this module (ADR-0010).
 */
public record NotebookCreated(UUID notebookId, UserId ownerId, Instant occurredAt) implements DomainEvent {

    public NotebookCreated {
        Objects.requireNonNull(notebookId, "notebookId must not be null");
        Objects.requireNonNull(ownerId, "ownerId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
