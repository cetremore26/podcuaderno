package com.podcuaderno.notebooks.adapter.out.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "topics")
class TopicJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "topic_id", nullable = false)
    @OrderBy("position")
    private List<ConceptJpaEntity> concepts = new ArrayList<>();

    protected TopicJpaEntity() {}

    TopicJpaEntity(UUID id, int position, String title, List<ConceptJpaEntity> concepts) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.concepts = new ArrayList<>(concepts);
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

    List<ConceptJpaEntity> getConcepts() {
        return concepts;
    }
}
