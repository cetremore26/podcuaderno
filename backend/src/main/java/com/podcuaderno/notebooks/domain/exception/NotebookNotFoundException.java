package com.podcuaderno.notebooks.domain.exception;

import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.exception.ResourceNotFoundException;

/** Missing notebook, or one owned by another user: both answer the same 404 (HU-06, R-06). */
public class NotebookNotFoundException extends ResourceNotFoundException {

    public NotebookNotFoundException(NotebookId id) {
        super("Notebook " + id.value() + " not found");
    }
}
