package com.podcuaderno.notebooks.application.port.in;

import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import java.time.Instant;
import java.util.Objects;

/** One row of the notebook list: no topics are loaded (docs/05 §12, lists use projections). */
public record NotebookSummary(NotebookId id, String title, Instant updatedAt) {

    public NotebookSummary {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static NotebookSummary of(Notebook notebook) {
        return new NotebookSummary(notebook.id(), notebook.title().value(), notebook.updatedAt());
    }
}
