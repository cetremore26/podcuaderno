package com.podcuaderno.notebooks.application.port.in;

import com.podcuaderno.shared.domain.UserId;
import java.util.Objects;

public record CreateNotebookCommand(UserId owner, String title) {

    public CreateNotebookCommand {
        Objects.requireNonNull(owner, "owner must not be null");
        Objects.requireNonNull(title, "title must not be null");
    }
}
