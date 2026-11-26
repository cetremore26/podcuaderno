package com.podcuaderno.identity.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consents")
class ConsentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "policy_version", nullable = false)
    private String policyVersion;

    @Column(name = "accepted_at", nullable = false)
    private Instant acceptedAt;

    protected ConsentJpaEntity() {}

    ConsentJpaEntity(UUID id, UUID userId, String policyVersion, Instant acceptedAt) {
        this.id = id;
        this.userId = userId;
        this.policyVersion = policyVersion;
        this.acceptedAt = acceptedAt;
    }

    UUID getId() {
        return id;
    }

    UUID getUserId() {
        return userId;
    }

    String getPolicyVersion() {
        return policyVersion;
    }

    Instant getAcceptedAt() {
        return acceptedAt;
    }
}
