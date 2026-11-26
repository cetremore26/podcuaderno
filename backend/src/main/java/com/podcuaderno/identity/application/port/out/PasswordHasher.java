package com.podcuaderno.identity.application.port.out;

import com.podcuaderno.identity.domain.model.PasswordHash;

/** Password hashing (Argon2id, ADR-0009). */
public interface PasswordHasher {

    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);

    /** A valid hash of no real password, compared against when the email is unknown so timing reveals nothing. */
    PasswordHash dummyHash();
}
