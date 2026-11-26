package com.podcuaderno.notebooks.application.port.in;

/** HU-06: deletes one of the requester's notebooks with its topics and concepts. */
public interface DeleteNotebookUseCase {

    void delete(DeleteNotebookCommand command);
}
