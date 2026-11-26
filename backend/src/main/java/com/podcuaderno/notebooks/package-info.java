/**
 * Notebooks: notebooks, topics and concepts, with their editing and ownership rules.
 * Communicates with other modules only through domain events (ADR-0003, ADR-0008).
 */
@ApplicationModule(displayName = "Notebooks", allowedDependencies = "shared")
package com.podcuaderno.notebooks;

import org.springframework.modulith.ApplicationModule;
