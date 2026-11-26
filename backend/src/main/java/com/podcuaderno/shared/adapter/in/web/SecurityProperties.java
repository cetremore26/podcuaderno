package com.podcuaderno.shared.adapter.in.web;

import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security settings (ADR-0009).
 *
 * @param jwtSecret base64 HMAC key of at least 32 bytes; when blank a random key is generated at startup
 * @param allowedOrigins frontend origins allowed by CORS
 */
@ConfigurationProperties("podcuaderno.security")
record SecurityProperties(
        @Nullable String jwtSecret, @Nullable List<String> allowedOrigins) {

    SecurityProperties {
        allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
}
