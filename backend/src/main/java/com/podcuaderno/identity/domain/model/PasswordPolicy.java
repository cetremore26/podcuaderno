package com.podcuaderno.identity.domain.model;

import com.podcuaderno.identity.domain.exception.InvalidPasswordException;

/** Passwords have between 8 and 128 characters (HU-01, docs/05 §5.7). */
public final class PasswordPolicy {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 128;

    private PasswordPolicy() {}

    public static void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH || rawPassword.length() > MAX_LENGTH) {
            throw new InvalidPasswordException();
        }
    }
}
