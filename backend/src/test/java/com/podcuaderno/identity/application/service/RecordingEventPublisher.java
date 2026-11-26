package com.podcuaderno.identity.application.service;

import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import com.podcuaderno.shared.domain.DomainEvent;
import java.util.ArrayList;
import java.util.List;

/** Fake of {@link DomainEventPublisher} that keeps the published events, for service tests. */
class RecordingEventPublisher implements DomainEventPublisher {

    private final List<DomainEvent> events = new ArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        events.add(event);
    }

    List<DomainEvent> events() {
        return List.copyOf(events);
    }
}
