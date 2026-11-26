package com.podcuaderno.notebooks.adapter.in.web;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import java.time.Instant;
import java.util.UUID;

record NotebookResponse(UUID id, String title, Instant updatedAt) {

    static NotebookResponse from(NotebookSummary summary) {
        return new NotebookResponse(summary.id().value(), summary.title(), summary.updatedAt());
    }
}
