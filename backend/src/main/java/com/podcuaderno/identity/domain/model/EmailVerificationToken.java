package com.podcuaderno.identity.domain.model;

import com.podcuaderno.identity.domain.exception.InvalidVerificationTokenException;
import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/** Single-use link that activates an account, valid for 24 hours (HU-01). Only its hash is stored. */
public final class EmailVerificationToken {

    public static final Duration TIME_TO_LIVE = Duration.ofHours(24);
    public static final Duration RESEND_COOLDOWN = Duration.ofMinutes(2);

    private final EmailVerificationTokenId id;
    private final UserId userId;
    private final TokenHash tokenHash;
    private final Instant createdAt;
    private final Instant expiresAt;
    private @Nullable Instant usedAt;

    private EmailVerificationToken(
            EmailVerificationTokenId id,
            UserId userId,
            TokenHash tokenHash,
            Instant createdAt,
            Instant expiresAt,
            @Nullable Instant usedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.usedAt = usedAt;
    }

    public static EmailVerificationToken issue(
            EmailVerificationTokenId id, UserId userId, TokenHash tokenHash, Instant now) {
        return new EmailVerificationToken(id, userId, tokenHash, now, now.plus(TIME_TO_LIVE), null);
    }

    public static EmailVerificationToken restore(
            EmailVerificationTokenId id,
            UserId userId,
            TokenHash tokenHash,
            Instant createdAt,
            Instant expiresAt,
            @Nullable Instant usedAt) {
        return new EmailVerificationToken(id, userId, tokenHash, createdAt, expiresAt, usedAt);
    }

    public boolean isUsable(Instant now) {
        return usedAt == null && now.isBefore(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    /** Consumes the token; an expired or already used token is rejected. */
    public void markUsed(Instant now) {
        if (!isUsable(now)) {
            throw new InvalidVerificationTokenException();
        }
        usedAt = now;
    }

    public EmailVerificationTokenId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public TokenHash tokenHash() {
        return tokenHash;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public Optional<Instant> usedAt() {
        return Optional.ofNullable(usedAt);
    }
}
