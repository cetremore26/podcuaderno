package com.podcuaderno.identity.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "users")
class UserJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "email_verified_at")
    private @Nullable Instant emailVerifiedAt;

    protected UserJpaEntity() {}

    UserJpaEntity(
            UUID id,
            String email,
            String passwordHash,
            String status,
            String role,
            Instant createdAt,
            @Nullable Instant emailVerifiedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
        this.emailVerifiedAt = emailVerifiedAt;
    }

    UUID getId() {
        return id;
    }

    String getEmail() {
        return email;
    }

    String getPasswordHash() {
        return passwordHash;
    }

    String getStatus() {
        return status;
    }

    String getRole() {
        return role;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    @Nullable Instant getEmailVerifiedAt() {
        return emailVerifiedAt;
    }
}
