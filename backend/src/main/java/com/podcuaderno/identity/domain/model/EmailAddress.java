package com.podcuaderno.identity.domain.model;

import com.podcuaderno.identity.domain.exception.InvalidEmailAddressException;
import java.util.Locale;
import java.util.Objects;

/**
 * Normalised email address: trimmed, lower case, at most 254 characters, exactly one {@code @} with a non-empty local
 * part and a domain containing a dot. No regular expression, to avoid backtracking (docs/05 §5.7).
 */
public record EmailAddress(String value) {

    public static final int MAX_LENGTH = 254;

    public EmailAddress {
        Objects.requireNonNull(value, "value must not be null");
        value = value.strip().toLowerCase(Locale.ROOT);
        if (!isWellFormed(value)) {
            throw new InvalidEmailAddressException();
        }
    }

    private static boolean isWellFormed(String candidate) {
        int at = candidate.indexOf('@');
        return candidate.length() <= MAX_LENGTH
                && at > 0
                && at == candidate.lastIndexOf('@')
                && at < candidate.length() - 1
                && candidate.indexOf('.', at) > at + 1
                && !candidate.endsWith(".")
                && candidate.chars().noneMatch(Character::isWhitespace);
    }

    @Override
    public String toString() {
        return "EmailAddress[***]"; // personal data stays out of logs (docs/05 §5.8)
    }
}
