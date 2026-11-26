package com.podcuaderno.notebooks.adapter.out.persistence;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.domain.model.Concept;
import com.podcuaderno.notebooks.domain.model.ConceptId;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.notebooks.domain.model.Title;
import com.podcuaderno.notebooks.domain.model.Topic;
import com.podcuaderno.notebooks.domain.model.TopicId;
import com.podcuaderno.shared.domain.UserId;

final class NotebookMapper {

    private NotebookMapper() {}

    static NotebookJpaEntity toNewEntity(Notebook notebook) {
        return new NotebookJpaEntity(
                notebook.id().value(),
                notebook.owner().value(),
                notebook.title().value(),
                notebook.createdAt(),
                notebook.updatedAt(),
                notebook.topics().stream().map(NotebookMapper::toEntity).toList());
    }

    private static TopicJpaEntity toEntity(Topic topic) {
        return new TopicJpaEntity(
                topic.id().value(),
                topic.position(),
                topic.title().value(),
                topic.concepts().stream().map(NotebookMapper::toEntity).toList());
    }

    private static ConceptJpaEntity toEntity(Concept concept) {
        return new ConceptJpaEntity(
                concept.id().value(), concept.position(), concept.title().value(), concept.text());
    }

    static Notebook toDomain(NotebookJpaEntity entity) {
        return Notebook.restore(
                new NotebookId(entity.getId()),
                new UserId(entity.getOwnerId()),
                new Title(entity.getTitle()),
                entity.getTopics().stream().map(NotebookMapper::toDomain).toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private static Topic toDomain(TopicJpaEntity entity) {
        return new Topic(
                new TopicId(entity.getId()),
                entity.getPosition(),
                new Title(entity.getTitle()),
                entity.getConcepts().stream().map(NotebookMapper::toDomain).toList());
    }

    private static Concept toDomain(ConceptJpaEntity entity) {
        return new Concept(
                new ConceptId(entity.getId()), entity.getPosition(), new Title(entity.getTitle()), entity.getText());
    }

    static NotebookSummary toSummary(NotebookSummaryView view) {
        return new NotebookSummary(new NotebookId(view.getId()), view.getTitle(), view.getUpdatedAt());
    }
}
