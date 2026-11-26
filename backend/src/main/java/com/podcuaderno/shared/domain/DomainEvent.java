package com.podcuaderno.shared.domain;

import java.time.Instant;

/**
 * Marker of every domain event published between modules. Events carry only {@code UUID} values or shared types,
 * never types internal to the publishing module (ADR-0010).
 */
public interface DomainEvent {

    Instant occurredAt();
}
