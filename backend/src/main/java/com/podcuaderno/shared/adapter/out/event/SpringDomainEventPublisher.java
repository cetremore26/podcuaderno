package com.podcuaderno.shared.adapter.out.event;

import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import com.podcuaderno.shared.domain.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Publishes domain events through Spring, so Spring Modulith stores them in the Event Publication Registry within the
 * current transaction (ADR-0008).
 */
@Component
class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    SpringDomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
