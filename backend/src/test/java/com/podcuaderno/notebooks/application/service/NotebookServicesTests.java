package com.podcuaderno.notebooks.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.podcuaderno.notebooks.application.port.in.CreateNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.DeleteNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookCommand;
import com.podcuaderno.notebooks.domain.event.NotebookCreated;
import com.podcuaderno.notebooks.domain.exception.InvalidTitleException;
import com.podcuaderno.notebooks.domain.exception.NotebookNotFoundException;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import com.podcuaderno.shared.domain.DomainEvent;
import com.podcuaderno.shared.domain.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotebookServicesTests {

    private static final Instant NOW = Instant.parse("2026-12-14T10:00:00Z");
    private static final UserId OWNER = UserId.newId();
    private static final UserId INTRUDER = UserId.newId();

    private final InMemoryNotebooks notebooks = new InMemoryNotebooks();
    private final List<DomainEvent> events = new ArrayList<>();
    private final DomainEventPublisher publisher = events::add;

    private CreateNotebookService createAt(Instant instant) {
        return new CreateNotebookService(notebooks, publisher, Clock.fixed(instant, ZoneOffset.UTC));
    }

    private RenameNotebookService renameAt(Instant instant) {
        return new RenameNotebookService(notebooks, Clock.fixed(instant, ZoneOffset.UTC));
    }

    @Test
    void createStoresAnEmptyNotebookAndPublishesTheEventWithTheOwner() {
        NotebookSummary created = createAt(NOW).create(new CreateNotebookCommand(OWNER, "  Cálculo "));

        assertThat(created.title()).isEqualTo("Cálculo");
        assertThat(created.updatedAt()).isEqualTo(NOW);
        assertThat(notebooks.findById(created.id()).orElseThrow().topics()).isEmpty();
        assertThat(events).containsExactly(new NotebookCreated(created.id().value(), OWNER, NOW));
    }

    @Test
    void createRejectsAnInvalidTitle() {
        assertThatThrownBy(() -> createAt(NOW).create(new CreateNotebookCommand(OWNER, " ")))
                .isInstanceOf(InvalidTitleException.class);
        assertThat(notebooks.count()).isZero();
        assertThat(events).isEmpty();
    }

    @Test
    void listReturnsOnlyTheOwnersNotebooksMostRecentFirst() {
        createAt(NOW).create(new CreateNotebookCommand(OWNER, "Antiguo"));
        createAt(NOW.plusSeconds(60)).create(new CreateNotebookCommand(OWNER, "Reciente"));
        createAt(NOW.plusSeconds(120)).create(new CreateNotebookCommand(INTRUDER, "Ajeno"));

        assertThat(new ListNotebooksService(notebooks).list(OWNER))
                .extracting(NotebookSummary::title)
                .containsExactly("Reciente", "Antiguo");
    }

    @Test
    void renameUpdatesTheTitleAndTheModificationDate() {
        NotebookId id = createAt(NOW)
                .create(new CreateNotebookCommand(OWNER, "Cálculo"))
                .id();

        NotebookSummary renamed =
                renameAt(NOW.plusSeconds(60)).rename(new RenameNotebookCommand(OWNER, id, "Cálculo integral"));

        assertThat(renamed.title()).isEqualTo("Cálculo integral");
        assertThat(renamed.updatedAt()).isEqualTo(NOW.plusSeconds(60));
    }

    @Test
    void renamingAnotherUsersNotebookLooksLikeAMissingOne() {
        NotebookId id = createAt(NOW)
                .create(new CreateNotebookCommand(OWNER, "Cálculo"))
                .id();

        assertThatThrownBy(() -> renameAt(NOW).rename(new RenameNotebookCommand(INTRUDER, id, "Ajeno")))
                .isInstanceOf(NotebookNotFoundException.class);
        assertThatThrownBy(() -> renameAt(NOW).rename(new RenameNotebookCommand(OWNER, NotebookId.newId(), "X")))
                .isInstanceOf(NotebookNotFoundException.class);
        assertThat(notebooks.findById(id).orElseThrow().title().value()).isEqualTo("Cálculo");
    }

    @Test
    void deleteRemovesOnlyTheOwnersNotebook() {
        NotebookId id = createAt(NOW)
                .create(new CreateNotebookCommand(OWNER, "Cálculo"))
                .id();
        DeleteNotebookService delete = new DeleteNotebookService(notebooks);

        assertThatThrownBy(() -> delete.delete(new DeleteNotebookCommand(INTRUDER, id)))
                .isInstanceOf(NotebookNotFoundException.class);
        assertThat(notebooks.count()).isOne();

        delete.delete(new DeleteNotebookCommand(OWNER, id));
        assertThat(notebooks.count()).isZero();
    }
}
