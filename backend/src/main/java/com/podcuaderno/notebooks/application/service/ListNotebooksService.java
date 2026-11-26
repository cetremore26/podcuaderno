package com.podcuaderno.notebooks.application.service;

import com.podcuaderno.notebooks.application.port.in.ListNotebooksUseCase;
import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class ListNotebooksService implements ListNotebooksUseCase {

    private final NotebookRepository notebooks;

    ListNotebooksService(NotebookRepository notebooks) {
        this.notebooks = notebooks;
    }

    @Override
    public List<NotebookSummary> list(UserId owner) {
        return notebooks.findSummariesByOwner(owner);
    }
}
