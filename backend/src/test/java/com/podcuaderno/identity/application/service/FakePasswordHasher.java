package com.podcuaderno.identity.application.service;

import com.podcuaderno.identity.application.port.out.PasswordHasher;
import com.podcuaderno.identity.domain.model.PasswordHash;

/** Reversible fake of {@link PasswordHasher} that counts its work, for service tests. */
class FakePasswordHasher implements PasswordHasher {

    private static final String PREFIX = "{fake}";
    private static final PasswordHash DUMMY = new PasswordHash("{fake-dummy}");

    private int hashes;
    private int comparisons;

    @Override
    public PasswordHash hash(String rawPassword) {
        hashes++;
        return new PasswordHash(PREFIX + rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash hash) {
        comparisons++;
        return hash.value().equals(PREFIX + rawPassword);
    }

    @Override
    public PasswordHash dummyHash() {
        return DUMMY;
    }

    int hashes() {
        return hashes;
    }

    int comparisons() {
        return comparisons;
    }
}
