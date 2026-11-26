package com.podcuaderno.notebooks.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "concepts")
class ConceptJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    protected ConceptJpaEntity() {}

    ConceptJpaEntity(UUID id, int position, String title, String text) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.text = text;
    }

    UUID getId() {
        return id;
    }

    int getPosition() {
        return position;
    }

    String getTitle() {
        return title;
    }

    String getText() {
        return text;
    }
}
