/**
 * Shared kernel: common types used by every module, global error handling and security configuration.
 *
 * <p>Open module (ADR-0008, amended by ADR-0010): {@code domain} holds framework-free types such as {@code UserId},
 * {@code DomainEvent} and the base exceptions in {@code domain.exception}; {@code application.port.out} holds the
 * {@code DomainEventPublisher} port and {@code adapter.out.event} its Spring implementation; {@code adapter.in.web}
 * holds the global error handling and the Spring Security configuration. It never depends on a business module.
 */
@ApplicationModule(displayName = "Shared kernel", type = ApplicationModule.Type.OPEN)
package com.podcuaderno.shared;

import org.springframework.modulith.ApplicationModule;
