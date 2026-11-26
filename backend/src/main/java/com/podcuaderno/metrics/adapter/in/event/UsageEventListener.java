package com.podcuaderno.metrics.adapter.in.event;

import com.podcuaderno.identity.domain.event.UserRegistered;
import com.podcuaderno.metrics.application.port.in.RecordUsageEventCommand;
import com.podcuaderno.metrics.application.port.in.RecordUsageEventUseCase;
import com.podcuaderno.metrics.domain.model.UsageEventType;
import com.podcuaderno.notebooks.domain.event.NotebookCreated;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/** Turns events of other modules into usage events, after their transaction commits (ADR-0003). */
@Component
class UsageEventListener {

    private final RecordUsageEventUseCase recordUsageEvent;

    UsageEventListener(RecordUsageEventUseCase recordUsageEvent) {
        this.recordUsageEvent = recordUsageEvent;
    }

    @ApplicationModuleListener
    void on(UserRegistered event) {
        recordUsageEvent.record(
                new RecordUsageEventCommand(event.userId(), UsageEventType.USER_REGISTERED, 0, event.occurredAt()));
    }

    @ApplicationModuleListener
    void on(NotebookCreated event) {
        recordUsageEvent.record(
                new RecordUsageEventCommand(event.ownerId(), UsageEventType.NOTEBOOK_CREATED, 0, event.occurredAt()));
    }
}
