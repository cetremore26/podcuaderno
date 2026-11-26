/**
 * Shared kernel: common types used by every module, global error handling and security configuration.
 *
 * <p>Open module (ADR-0008) with two areas: {@code domain} holds framework-free types such as {@code UserId} and the
 * base exceptions in {@code domain.exception}; {@code adapter.in.web} holds the global error handling and the Spring
 * Security configuration. It never depends on a business module.
 */
@ApplicationModule(displayName = "Shared kernel", type = ApplicationModule.Type.OPEN)
package com.podcuaderno.shared;

import org.springframework.modulith.ApplicationModule;
