package com.podcuaderno.identity.domain.model;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/** Account of a student. It stays pending until the email address is verified (HU-01). */
public final class User {

    private final UserId id;
    private final EmailAddress email;
    private final PasswordHash passwordHash;
    private final Role role;
    private final Instant createdAt;
    private UserStatus status;
    private @Nullable Instant emailVerifiedAt;

    private User(
            UserId id,
            EmailAddress email,
            PasswordHash passwordHash,
            UserStatus status,
            Role role,
            Instant createdAt,
            @Nullable Instant emailVerifiedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.emailVerifiedAt = emailVerifiedAt;
    }

    /** A new student account, pending verification. */
    public static User register(UserId id, EmailAddress email, PasswordHash passwordHash, Instant now) {
        return new User(id, email, passwordHash, UserStatus.PENDING_VERIFICATION, Role.STUDENT, now, null);
    }

    /** Rebuilds a stored account. */
    public static User restore(
            UserId id,
            EmailAddress email,
            PasswordHash passwordHash,
            UserStatus status,
            Role role,
            Instant createdAt,
            @Nullable Instant emailVerifiedAt) {
        return new User(id, email, passwordHash, status, role, createdAt, emailVerifiedAt);
    }

    /** Activates the account. Verifying an active account again changes nothing. */
    public void verifyEmail(Instant now) {
        if (status == UserStatus.ACTIVE) {
            return;
        }
        status = UserStatus.ACTIVE;
        emailVerifiedAt = Objects.requireNonNull(now, "now must not be null");
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isPendingVerification() {
        return status == UserStatus.PENDING_VERIFICATION;
    }

    public UserId id() {
        return id;
    }

    public EmailAddress email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public UserStatus status() {
        return status;
    }

    public Role role() {
        return role;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Optional<Instant> emailVerifiedAt() {
        return Optional.ofNullable(emailVerifiedAt);
    }
}
