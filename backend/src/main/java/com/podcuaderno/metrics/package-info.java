/**
 * Metrics: records aggregated usage events and computes the dashboard metrics. Listens to the events of identity and
 * notebooks; never stores content (ADR-0003, ADR-0008, ADR-0010).
 */
@ApplicationModule(
        displayName = "Metrics",
        allowedDependencies = {"shared", "identity :: events", "notebooks :: events"})
package com.podcuaderno.metrics;

import org.springframework.modulith.ApplicationModule;
