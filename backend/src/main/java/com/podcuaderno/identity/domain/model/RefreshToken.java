package com.podcuaderno.identity.domain.model;

import com.podcuaderno.shared.domain.UserId;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Long-lived session credential sent in an HttpOnly cookie. Only its hash is stored; every renewal rotates it within
 * the same family, so the reuse of a rotated token reveals a theft (ADR-0009).
 */
public final class RefreshToken {

    public static final Duration TIME_TO_LIVE = Duration.ofDays(14);

    private final RefreshTokenId id;
    private final UserId userId;
    private final RefreshTokenFamilyId familyId;
    private final TokenHash tokenHash;
    private final Instant createdAt;
    private final Instant expiresAt;
    private @Nullable Instant rotatedAt;
    private @Nullable Instant revokedAt;

    private RefreshToken(
            RefreshTokenId id,
            UserId userId,
            RefreshTokenFamilyId familyId,
            TokenHash tokenHash,
            Instant createdAt,
            Instant expiresAt,
            @Nullable Instant rotatedAt,
            @Nullable Instant revokedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.familyId = Objects.requireNonNull(familyId, "familyId must not be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.rotatedAt = rotatedAt;
        this.revokedAt = revokedAt;
    }

    /** First token of a new session (a new family). */
    public static RefreshToken issue(UserId userId, TokenHash tokenHash, Instant now) {
        return new RefreshToken(
                RefreshTokenId.newId(),
                userId,
                RefreshTokenFamilyId.newId(),
                tokenHash,
                now,
                now.plus(TIME_TO_LIVE),
                null,
                null);
    }

    public static RefreshToken restore(
            RefreshTokenId id,
            UserId userId,
            RefreshTokenFamilyId familyId,
            TokenHash tokenHash,
            Instant createdAt,
            Instant expiresAt,
            @Nullable Instant rotatedAt,
            @Nullable Instant revokedAt) {
        return new RefreshToken(id, userId, familyId, tokenHash, createdAt, expiresAt, rotatedAt, revokedAt);
    }

    /**
     * Replaces this token with a new one of the same family. This token stays stored as rotated, so presenting it again
     * reveals a theft.
     */
    public RefreshToken rotate(TokenHash newTokenHash, Instant now) {
        if (!isUsable(now)) {
            throw new IllegalStateException("Only a usable refresh token can be rotated");
        }
        rotatedAt = now;
        return new RefreshToken(
                RefreshTokenId.newId(), userId, familyId, newTokenHash, now, now.plus(TIME_TO_LIVE), null, null);
    }

    public boolean isUsable(Instant now) {
        return rotatedAt == null && revokedAt == null && now.isBefore(expiresAt);
    }

    public boolean wasRotated() {
        return rotatedAt != null;
    }

    public RefreshTokenId id() {
        return id;
    }

    public UserId userId() {
        return userId;
    }

    public RefreshTokenFamilyId familyId() {
        return familyId;
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

    public Optional<Instant> rotatedAt() {
        return Optional.ofNullable(rotatedAt);
    }

    public Optional<Instant> revokedAt() {
        return Optional.ofNullable(revokedAt);
    }
}
