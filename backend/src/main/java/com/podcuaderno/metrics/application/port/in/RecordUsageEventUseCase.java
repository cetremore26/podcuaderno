package com.podcuaderno.metrics.application.port.in;

/** Stores one usage event. */
public interface RecordUsageEventUseCase {

    void record(RecordUsageEventCommand command);
}
