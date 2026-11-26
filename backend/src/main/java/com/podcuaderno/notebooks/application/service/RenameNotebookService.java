package com.podcuaderno.notebooks.application.service;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookUseCase;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.exception.NotebookNotFoundException;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.Title;
import java.time.Clock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class RenameNotebookService implements RenameNotebookUseCase {

    private final NotebookRepository notebooks;
    private final Clock clock;

    RenameNotebookService(NotebookRepository notebooks, Clock clock) {
        this.notebooks = notebooks;
        this.clock = clock;
    }

    @Override
    public NotebookSummary rename(RenameNotebookCommand command) {
        Title title = new Title(command.title());
        // Another user's notebook answers exactly like a missing one (HU-06, docs/05 §5.4).
        Notebook notebook = notebooks
                .findById(command.notebookId())
                .filter(n -> n.isOwnedBy(command.requester()))
                .orElseThrow(() -> new NotebookNotFoundException(command.notebookId()));
        notebook.rename(title, clock.instant());
        notebooks.save(notebook);
        return NotebookSummary.of(notebook);
    }
}
