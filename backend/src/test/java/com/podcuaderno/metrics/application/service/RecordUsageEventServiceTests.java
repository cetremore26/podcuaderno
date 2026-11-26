package com.podcuaderno.metrics.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.metrics.application.port.in.RecordUsageEventCommand;
import com.podcuaderno.metrics.domain.model.UsageEvent;
import com.podcuaderno.metrics.domain.model.UsageEventType;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RecordUsageEventServiceTests {

    @Test
    void storesTheEventWithItsOriginalInstant() {
        List<UsageEvent> stored = new ArrayList<>();
        RecordUsageEventService service = new RecordUsageEventService(stored::add);
        UserId user = UserId.newId();
        Instant occurredAt = Instant.parse("2026-12-14T10:00:00Z");

        service.record(new RecordUsageEventCommand(user, UsageEventType.USER_REGISTERED, 0, occurredAt));

        assertThat(stored).singleElement().satisfies(event -> {
            assertThat(event.userId()).isEqualTo(user);
            assertThat(event.type()).isEqualTo(UsageEventType.USER_REGISTERED);
            assertThat(event.characters()).isZero();
            assertThat(event.occurredAt()).isEqualTo(occurredAt);
        });
    }
}
