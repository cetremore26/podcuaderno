package com.podcuaderno.notebooks.application.service;

import com.podcuaderno.notebooks.application.port.in.DeleteNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.DeleteNotebookUseCase;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.exception.NotebookNotFoundException;
import com.podcuaderno.notebooks.domain.model.Notebook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class DeleteNotebookService implements DeleteNotebookUseCase {

    private final NotebookRepository notebooks;

    DeleteNotebookService(NotebookRepository notebooks) {
        this.notebooks = notebooks;
    }

    @Override
    public void delete(DeleteNotebookCommand command) {
        // Another user's notebook answers exactly like a missing one (HU-06, docs/05 §5.4).
        Notebook notebook = notebooks
                .findById(command.notebookId())
                .filter(n -> n.isOwnedBy(command.requester()))
                .orElseThrow(() -> new NotebookNotFoundException(command.notebookId()));
        notebooks.delete(notebook.id());
    }
}
