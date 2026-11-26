package com.podcuaderno.notebooks.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notebooks")
class NotebookJpaEntity {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String title;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "notebook_id", nullable = false)
    @OrderBy("position")
    private List<TopicJpaEntity> topics = new ArrayList<>();

    protected NotebookJpaEntity() {}

    NotebookJpaEntity(
            UUID id, UUID ownerId, String title, Instant createdAt, Instant updatedAt, List<TopicJpaEntity> topics) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.topics = new ArrayList<>(topics);
    }

    void rename(String newTitle, Instant newUpdatedAt) {
        this.title = newTitle;
        this.updatedAt = newUpdatedAt;
    }

    UUID getId() {
        return id;
    }

    UUID getOwnerId() {
        return ownerId;
    }

    String getTitle() {
        return title;
    }

    Instant getCreatedAt() {
        return createdAt;
    }

    Instant getUpdatedAt() {
        return updatedAt;
    }

    List<TopicJpaEntity> getTopics() {
        return topics;
    }
}
