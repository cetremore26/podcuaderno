package com.podcuaderno.identity.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "rotated_at")
    private @Nullable Instant rotatedAt;

    @Column(name = "revoked_at")
    private @Nullable Instant revokedAt;

    protected RefreshTokenJpaEntity() {}

    RefreshTokenJpaEntity(
            UUID id,
            UUID userId,
            UUID familyId,
            String tokenHash,
            Instant createdAt,
            Instant expiresAt,
            @Nullable Instant rotatedAt,
            @Nullable Instant revokedAt) {
        this.id = id;
        this.userId = userId;
        this.familyId = familyId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.rotatedAt = rotatedAt;
        this.revokedAt = revokedAt;
    }

    UUID getId() {
        return id;
    }

    UUID getUserId() {
        return userId;
    }

    UUID getFamilyId() {
        return familyId;
    }

    String getTokenHash() {
        return tokenHash;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getExpiresAt() {
        return expiresAt;
    }

    @Nullable Instant getRotatedAt() {
        return rotatedAt;
    }

    @Nullable Instant getRevokedAt() {
        return revokedAt;
    }
}
