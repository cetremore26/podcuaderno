package com.podcuaderno.metrics.application.service;

import com.podcuaderno.metrics.application.port.in.RecordUsageEventCommand;
import com.podcuaderno.metrics.application.port.in.RecordUsageEventUseCase;
import com.podcuaderno.metrics.application.port.out.UsageEventRepository;
import com.podcuaderno.metrics.domain.model.UsageEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class RecordUsageEventService implements RecordUsageEventUseCase {

    private final UsageEventRepository usageEvents;

    RecordUsageEventService(UsageEventRepository usageEvents) {
        this.usageEvents = usageEvents;
    }

    @Override
    public void record(RecordUsageEventCommand command) {
        usageEvents.save(
                UsageEvent.occurred(command.userId(), command.type(), command.characters(), command.occurredAt()));
    }
}
