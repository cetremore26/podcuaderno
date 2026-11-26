package com.podcuaderno.notebooks.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.podcuaderno.IntegrationTest;
import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.notebooks.domain.model.Title;
import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

@IntegrationTest
class NotebookPersistenceIntegrationTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");

    @Autowired
    private NotebookRepository notebooks;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private TransactionTemplate transaction;

    // The adapter runs inside the transaction of an application service; tests open one the same way.
    private <T> T inTransaction(Supplier<T> work) {
        return transaction.execute(status -> work.get());
    }

    private void run(Runnable work) {
        transaction.executeWithoutResult(status -> work.run());
    }

    private Notebook saved(UserId owner, String title, Instant at) {
        Notebook notebook = Notebook.create(NotebookId.newId(), owner, new Title(title), at);
        run(() -> notebooks.save(notebook));
        return notebook;
    }

    private UUID insertTopicWithTwoConcepts(NotebookId notebookId) {
        UUID topicId = UUID.randomUUID();
        jdbc.update(
                "insert into topics (id, notebook_id, position, title) values (?, ?, 0, 'Tema 1')",
                topicId,
                notebookId.value());
        jdbc.update(
                "insert into concepts (id, topic_id, position, title, text) values (?, ?, 1, 'Derivadas', 'Texto B')",
                UUID.randomUUID(),
                topicId);
        jdbc.update(
                "insert into concepts (id, topic_id, position, title, text) values (?, ?, 0, 'Límites', 'Texto A')",
                UUID.randomUUID(),
                topicId);
        return topicId;
    }

    @Test
    void savesAndReadsANotebook() {
        UserId owner = UserId.newId();
        Notebook notebook = saved(owner, "Cálculo", NOW);

        Notebook found = inTransaction(() -> notebooks.findById(notebook.id()).orElseThrow());

        assertThat(found.owner()).isEqualTo(owner);
        assertThat(found.title().value()).isEqualTo("Cálculo");
        assertThat(found.createdAt()).isEqualTo(NOW);
        assertThat(found.topics()).isEmpty();
        assertThat(inTransaction(() -> notebooks.findById(NotebookId.newId()))).isEmpty();
    }

    @Test
    void savingAnExistingNotebookRenamesIt() {
        Notebook notebook = saved(UserId.newId(), "Cálculo", NOW);
        notebook.rename(new Title("Cálculo integral"), NOW.plusSeconds(60));

        run(() -> notebooks.save(notebook));

        Notebook found = inTransaction(() -> notebooks.findById(notebook.id()).orElseThrow());
        assertThat(found.title().value()).isEqualTo("Cálculo integral");
        assertThat(found.updatedAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @Test
    void summariesAreTheOwnersOnlyAndMostRecentFirst() {
        UserId owner = UserId.newId();
        saved(owner, "Antiguo", NOW);
        saved(owner, "Reciente", NOW.plusSeconds(120));
        saved(owner, "Medio", NOW.plusSeconds(60));
        saved(UserId.newId(), "Ajeno", NOW.plusSeconds(500));

        assertThat(inTransaction(() -> notebooks.findSummariesByOwner(owner)))
                .extracting(NotebookSummary::title)
                .containsExactly("Reciente", "Medio", "Antiguo");
    }

    @Test
    void readsTopicsAndConceptsInOrder() {
        Notebook notebook = saved(UserId.newId(), "Cálculo", NOW);
        insertTopicWithTwoConcepts(notebook.id());

        Notebook found = inTransaction(() -> notebooks.findById(notebook.id()).orElseThrow());

        assertThat(found.topics()).singleElement().satisfies(topic -> {
            assertThat(topic.title().value()).isEqualTo("Tema 1");
            assertThat(topic.concepts())
                    .extracting(concept -> concept.title().value())
                    .containsExactly("Límites", "Derivadas");
            assertThat(topic.concepts().getFirst().text()).isEqualTo("Texto A");
        });
    }

    @Test
    void deletingANotebookCascadesToItsTopicsAndConcepts() {
        Notebook notebook = saved(UserId.newId(), "Cálculo", NOW);
        UUID topicId = insertTopicWithTwoConcepts(notebook.id());

        run(() -> notebooks.delete(notebook.id()));

        assertThat(inTransaction(() -> notebooks.findById(notebook.id()))).isEmpty();
        assertThat(jdbc.queryForObject(
                        "select count(*) from topics where notebook_id = ?",
                        Integer.class,
                        notebook.id().value()))
                .isZero();
        assertThat(jdbc.queryForObject("select count(*) from concepts where topic_id = ?", Integer.class, topicId))
                .isZero();
    }
}
