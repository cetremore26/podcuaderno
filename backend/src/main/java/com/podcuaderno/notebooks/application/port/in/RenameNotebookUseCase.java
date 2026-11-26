package com.podcuaderno.notebooks.application.port.in;

/** HU-06: renames one of the requester's notebooks; another user's notebook is reported as missing. */
public interface RenameNotebookUseCase {

    NotebookSummary rename(RenameNotebookCommand command);
}
