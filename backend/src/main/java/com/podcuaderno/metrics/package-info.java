/**
 * Metrics: records aggregated usage events and computes the dashboard metrics.
 * Communicates with other modules only through domain events (ADR-0003, ADR-0008).
 */
@ApplicationModule(displayName = "Metrics", allowedDependencies = "shared")
package com.podcuaderno.metrics;

import org.springframework.modulith.ApplicationModule;
