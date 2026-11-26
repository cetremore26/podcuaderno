package com.podcuaderno.notebooks.application.port.in;

import com.podcuaderno.shared.domain.UserId;
import java.util.List;

/** HU-06: the requester's notebooks, most recently modified first. */
public interface ListNotebooksUseCase {

    List<NotebookSummary> list(UserId owner);
}
