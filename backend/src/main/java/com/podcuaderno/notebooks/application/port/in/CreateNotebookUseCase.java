package com.podcuaderno.notebooks.application.port.in;

/** HU-06: creates an empty notebook with a title. */
public interface CreateNotebookUseCase {

    NotebookSummary create(CreateNotebookCommand command);
}
