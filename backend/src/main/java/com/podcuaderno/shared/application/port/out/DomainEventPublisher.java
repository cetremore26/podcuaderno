package com.podcuaderno.shared.application.port.out;

import com.podcuaderno.shared.domain.DomainEvent;

/** Outbound port used by application services to publish domain events (ADR-0003, ADR-0010). */
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
