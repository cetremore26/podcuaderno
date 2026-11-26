package com.podcuaderno.notebooks.application.service;

import com.podcuaderno.notebooks.application.port.in.CreateNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.CreateNotebookUseCase;
import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.event.NotebookCreated;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.notebooks.domain.model.Title;
import com.podcuaderno.shared.application.port.out.DomainEventPublisher;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class CreateNotebookService implements CreateNotebookUseCase {

    private final NotebookRepository notebooks;
    private final DomainEventPublisher events;
    private final Clock clock;

    CreateNotebookService(NotebookRepository notebooks, DomainEventPublisher events, Clock clock) {
        this.notebooks = notebooks;
        this.events = events;
        this.clock = clock;
    }

    @Override
    public NotebookSummary create(CreateNotebookCommand command) {
        Instant now = clock.instant();
        Notebook notebook = Notebook.create(NotebookId.newId(), command.owner(), new Title(command.title()), now);
        notebooks.save(notebook);
        events.publish(new NotebookCreated(notebook.id().value(), notebook.owner(), now));
        return NotebookSummary.of(notebook);
    }
}
