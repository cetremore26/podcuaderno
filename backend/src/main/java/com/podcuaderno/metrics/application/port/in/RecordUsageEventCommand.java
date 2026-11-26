package com.podcuaderno.metrics.application.port.in;

import com.podcuaderno.metrics.domain.model.UsageEventType;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;

public record RecordUsageEventCommand(UserId userId, UsageEventType type, int characters, Instant occurredAt) {

    public RecordUsageEventCommand {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
