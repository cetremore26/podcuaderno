package com.podcuaderno.metrics.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "usage_events")
class UsageEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int characters;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected UsageEventJpaEntity() {}

    UsageEventJpaEntity(UUID id, UUID userId, String type, int characters, Instant occurredAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.characters = characters;
        this.occurredAt = occurredAt;
    }

    UUID getId() {
        return id;
    }

    UUID getUserId() {
        return userId;
    }

    String getType() {
        return type;
    }

    int getCharacters() {
        return characters;
    }

    Instant getOccurredAt() {
        return occurredAt;
    }
}
