/**
 * Identity: registration, email verification, login, password recovery, account deletion and consents.
 * Communicates with other modules only through domain events (ADR-0003, ADR-0008).
 */
@ApplicationModule(displayName = "Identity", allowedDependencies = "shared")
package com.podcuaderno.identity;

import org.springframework.modulith.ApplicationModule;
