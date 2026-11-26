package com.podcuaderno.shared.adapter.in.web;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * HS256 access tokens: one service issues and validates them, so a shared HMAC key is enough (ADR-0009). Modules
 * inject the {@link JwtEncoder} bean, never this class.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SecurityProperties.class)
class JwtConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtConfiguration.class);
    private static final int MIN_KEY_BYTES = 32;
    private static final String HMAC_SHA256 = "HmacSHA256";

    @Bean
    SecretKey accessTokenSigningKey(SecurityProperties properties) {
        String secret = properties.jwtSecret();
        if (secret == null || secret.isBlank()) {
            log.warn("No JWT secret configured; using a random key. Access tokens will not survive a restart.");
            byte[] random = new byte[MIN_KEY_BYTES];
            new SecureRandom().nextBytes(random);
            return new SecretKeySpec(random, HMAC_SHA256);
        }
        byte[] decoded = Base64.getDecoder().decode(secret.strip());
        if (decoded.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("podcuaderno.security.jwt-secret must decode to at least 32 bytes");
        }
        return new SecretKeySpec(decoded, HMAC_SHA256);
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey accessTokenSigningKey) {
        return NimbusJwtEncoder.withSecretKey(accessTokenSigningKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey accessTokenSigningKey) {
        return NimbusJwtDecoder.withSecretKey(accessTokenSigningKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
