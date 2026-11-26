package com.podcuaderno.identity.adapter.out.security;

import com.podcuaderno.identity.application.port.out.PasswordHasher;
import com.podcuaderno.identity.domain.model.OpaqueToken;
import com.podcuaderno.identity.domain.model.PasswordHash;
import java.util.Map;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Argon2id with the OWASP minimum parameters: m = 19 MiB, t = 2, p = 1 (ADR-0009). The {@code {argon2id}} prefix
 * lets the algorithm change later without invalidating stored hashes.
 */
@Component
final class Argon2PasswordHasher implements PasswordHasher {

    private static final String ENCODING_ID = "argon2id";
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY_KIB = 19_456;
    private static final int ITERATIONS = 2;

    private final PasswordEncoder encoder = new DelegatingPasswordEncoder(
            ENCODING_ID,
            Map.of(
                    ENCODING_ID,
                    new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY_KIB, ITERATIONS)));

    // Hash of a random secret nobody knows: compared against when the email is unknown, so timing reveals nothing.
    private final PasswordHash dummyHash = hash(OpaqueToken.generate().value());

    @Override
    public PasswordHash hash(String rawPassword) {
        return new PasswordHash(encoder.encode(rawPassword));
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        return encoder.matches(rawPassword, hash.value());
    }

    @Override
    public PasswordHash dummyHash() {
        return dummyHash;
    }
}
