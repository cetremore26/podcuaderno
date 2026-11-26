package com.podcuaderno.identity.domain.model;

/** Lifecycle of an account: pending until the email address is verified (HU-01). */
public enum UserStatus {
    PENDING_VERIFICATION,
    ACTIVE
}
