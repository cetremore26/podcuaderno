package com.podcuaderno.shared.adapter.out.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.podcuaderno.shared.domain.DomainEvent;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class SpringDomainEventPublisherTests {

    @Test
    void delegatesToTheSpringEventPublisher() {
        ApplicationEventPublisher springPublisher = mock(ApplicationEventPublisher.class);
        SpringDomainEventPublisher publisher = new SpringDomainEventPublisher(springPublisher);
        DomainEvent event = new SampleEvent(Instant.parse("2026-12-14T10:00:00Z"));

        publisher.publish(event);

        verify(springPublisher).publishEvent(event);
    }

    record SampleEvent(Instant occurredAt) implements DomainEvent {}
}
