package com.podcuaderno.notebooks.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

/** Interface projection of the notebook list: reads three columns and no topics. */
interface NotebookSummaryView {

    UUID getId();

    String getTitle();

    Instant getUpdatedAt();
}
