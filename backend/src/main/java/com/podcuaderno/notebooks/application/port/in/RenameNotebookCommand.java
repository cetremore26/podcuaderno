package com.podcuaderno.notebooks.application.port.in;

import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.UserId;
import java.util.Objects;

public record RenameNotebookCommand(UserId requester, NotebookId notebookId, String title) {

    public RenameNotebookCommand {
        Objects.requireNonNull(requester, "requester must not be null");
        Objects.requireNonNull(notebookId, "notebookId must not be null");
        Objects.requireNonNull(title, "title must not be null");
    }
}
