package com.podcuaderno.notebooks.application.port.out;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import java.util.Optional;

public interface NotebookRepository {

    Optional<Notebook> findById(NotebookId id);

    /** The owner's notebooks, most recently modified first. */
    List<NotebookSummary> findSummariesByOwner(UserId owner);

    void save(Notebook notebook);

    /** Deletes the notebook; the database cascades to its topics and concepts. */
    void delete(NotebookId id);
}
