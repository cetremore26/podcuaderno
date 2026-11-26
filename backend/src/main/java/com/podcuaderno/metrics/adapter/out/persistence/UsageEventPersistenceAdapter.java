package com.podcuaderno.metrics.adapter.out.persistence;

import com.podcuaderno.metrics.application.port.out.UsageEventRepository;
import com.podcuaderno.metrics.domain.model.UsageEvent;
import org.springframework.stereotype.Component;

@Component
class UsageEventPersistenceAdapter implements UsageEventRepository {

    private final UsageEventJpaRepository jpa;

    UsageEventPersistenceAdapter(UsageEventJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(UsageEvent event) {
        jpa.save(new UsageEventJpaEntity(
                event.id().value(),
                event.userId().value(),
                event.type().name(),
                event.characters(),
                event.occurredAt()));
    }
}
