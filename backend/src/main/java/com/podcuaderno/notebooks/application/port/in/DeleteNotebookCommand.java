package com.podcuaderno.notebooks.application.port.in;

import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.UserId;
import java.util.Objects;

public record DeleteNotebookCommand(UserId requester, NotebookId notebookId) {

    public DeleteNotebookCommand {
        Objects.requireNonNull(requester, "requester must not be null");
        Objects.requireNonNull(notebookId, "notebookId must not be null");
    }
}
