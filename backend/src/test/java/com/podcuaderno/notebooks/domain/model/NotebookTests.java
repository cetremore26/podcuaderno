package com.podcuaderno.notebooks.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.notebooks.domain.event.NotebookCreated;
import com.podcuaderno.notebooks.domain.exception.InvalidTitleException;
import com.podcuaderno.notebooks.domain.exception.NotebookNotFoundException;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NotebookTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final UserId OWNER = UserId.newId();

    @Test
    void aNewNotebookIsEmptyAndOwned() {
        NotebookId id = NotebookId.newId();

        Notebook notebook = Notebook.create(id, OWNER, new Title("Cálculo"), NOW);

        assertThat(notebook.id()).isEqualTo(id);
        assertThat(notebook.owner()).isEqualTo(OWNER);
        assertThat(notebook.title().value()).isEqualTo("Cálculo");
        assertThat(notebook.topics()).isEmpty();
        assertThat(notebook.createdAt()).isEqualTo(NOW);
        assertThat(notebook.updatedAt()).isEqualTo(NOW);
        assertThat(notebook.isOwnedBy(OWNER)).isTrue();
        assertThat(notebook.isOwnedBy(UserId.newId())).isFalse();
    }

    @Test
    void renamingChangesTheTitleAndTheModificationDate() {
        Notebook notebook = Notebook.create(NotebookId.newId(), OWNER, new Title("Cálculo"), NOW);

        notebook.rename(new Title("Cálculo integral"), NOW.plusSeconds(60));

        assertThat(notebook.title().value()).isEqualTo("Cálculo integral");
        assertThat(notebook.updatedAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @Test
    void renamingToTheSameTitleIsNotAModification() {
        Notebook notebook = Notebook.create(NotebookId.newId(), OWNER, new Title("Cálculo"), NOW);

        notebook.rename(new Title("  Cálculo "), NOW.plusSeconds(60));

        assertThat(notebook.updatedAt()).isEqualTo(NOW);
    }

    @Test
    void restoreKeepsTheStructureAndProtectsIt() {
        Concept concept = new Concept(ConceptId.newId(), 0, new Title("Límites"), "Texto");
        List<Topic> topics =
                new ArrayList<>(List.of(new Topic(TopicId.newId(), 0, new Title("Tema 1"), List.of(concept))));

        Notebook notebook =
                Notebook.restore(NotebookId.newId(), OWNER, new Title("Cálculo"), topics, NOW, NOW.plusSeconds(5));
        topics.clear();

        assertThat(notebook.topics())
                .singleElement()
                .satisfies(topic -> assertThat(topic.concepts()).containsExactly(concept));
        assertThat(notebook.updatedAt()).isEqualTo(NOW.plusSeconds(5));
    }

    @Test
    void requiresItsMandatoryFields() {
        NotebookId id = NotebookId.newId();
        Title title = new Title("Cálculo");

        assertThatThrownBy(() -> Notebook.create(null, OWNER, title, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Notebook.create(id, null, title, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Notebook.create(id, OWNER, null, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Notebook.create(id, OWNER, title, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Notebook.restore(id, OWNER, title, null, NOW, NOW))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Notebook.restore(id, OWNER, title, List.of(), NOW, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void titlesAreTrimmedAndLimitedTo150Characters() {
        assertThat(new Title("  Física ").value()).isEqualTo("Física");
        assertThat(new Title("a".repeat(150)).value()).hasSize(150);
        assertThatThrownBy(() -> new Title("a".repeat(151)))
                .isInstanceOf(InvalidTitleException.class)
                .extracting("code")
                .isEqualTo("invalid-title");
        assertThatThrownBy(() -> new Title(null)).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t"})
    void blankTitlesAreRejected(String value) {
        assertThatThrownBy(() -> new Title(value)).isInstanceOf(InvalidTitleException.class);
    }

    @Test
    void topicsAndConceptsValidateTheirFields() {
        Title title = new Title("Tema");
        TopicId topicId = TopicId.newId();
        ConceptId conceptId = ConceptId.newId();

        assertThatThrownBy(() -> new Topic(topicId, -1, title, List.of())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Topic(null, 0, title, List.of())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Topic(topicId, 0, null, List.of())).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Topic(topicId, 0, title, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Concept(conceptId, -1, title, "x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Concept(null, 0, title, "x")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Concept(conceptId, 0, null, "x")).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Concept(conceptId, 0, title, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void identifiersAreGeneratedAndRequired() {
        assertThat(NotebookId.newId()).isNotEqualTo(NotebookId.newId());
        assertThat(TopicId.newId()).isNotEqualTo(TopicId.newId());
        assertThat(ConceptId.newId()).isNotEqualTo(ConceptId.newId());
        assertThatThrownBy(() -> new NotebookId(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new TopicId(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ConceptId(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void notebookCreatedCarriesOnlyIdentifiersAndTheInstant() {
        UUID id = UUID.randomUUID();

        NotebookCreated event = new NotebookCreated(id, OWNER, NOW);

        assertThat(event.notebookId()).isEqualTo(id);
        assertThat(event.ownerId()).isEqualTo(OWNER);
        assertThat(event.occurredAt()).isEqualTo(NOW);
        assertThatThrownBy(() -> new NotebookCreated(null, OWNER, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NotebookCreated(id, null, NOW)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new NotebookCreated(id, OWNER, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void notFoundIsAResourceNotFound() {
        assertThat(new NotebookNotFoundException(NotebookId.newId())).hasMessageContaining("not found");
    }
}
