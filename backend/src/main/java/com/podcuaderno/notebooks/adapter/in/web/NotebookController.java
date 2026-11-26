package com.podcuaderno.notebooks.adapter.in.web;

import com.podcuaderno.notebooks.application.port.in.CreateNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.CreateNotebookUseCase;
import com.podcuaderno.notebooks.application.port.in.DeleteNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.DeleteNotebookUseCase;
import com.podcuaderno.notebooks.application.port.in.ListNotebooksUseCase;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookCommand;
import com.podcuaderno.notebooks.application.port.in.RenameNotebookUseCase;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.adapter.in.web.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notebooks")
@Tag(name = "Notebooks", description = "The student's notebooks (HU-06)")
class NotebookController {

    private final CreateNotebookUseCase createNotebook;
    private final ListNotebooksUseCase listNotebooks;
    private final RenameNotebookUseCase renameNotebook;
    private final DeleteNotebookUseCase deleteNotebook;

    NotebookController(
            CreateNotebookUseCase createNotebook,
            ListNotebooksUseCase listNotebooks,
            RenameNotebookUseCase renameNotebook,
            DeleteNotebookUseCase deleteNotebook) {
        this.createNotebook = createNotebook;
        this.listNotebooks = listNotebooks;
        this.renameNotebook = renameNotebook;
        this.deleteNotebook = deleteNotebook;
    }

    @GetMapping
    @Operation(summary = "List my notebooks", description = "Only the requester's notebooks, most recent first.")
    List<NotebookResponse> list(@Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user) {
        return listNotebooks.list(user.id()).stream()
                .map(NotebookResponse::from)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Create an empty notebook")
    ResponseEntity<NotebookResponse> create(
            @Valid @RequestBody NotebookTitleRequest body,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user) {
        NotebookResponse created =
                NotebookResponse.from(createNotebook.create(new CreateNotebookCommand(user.id(), body.title())));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{notebookId}")
    @Operation(summary = "Rename a notebook", description = "Another user's notebook answers 404, like a missing one.")
    NotebookResponse rename(
            @PathVariable UUID notebookId,
            @Valid @RequestBody NotebookTitleRequest body,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user) {
        return NotebookResponse.from(
                renameNotebook.rename(new RenameNotebookCommand(user.id(), new NotebookId(notebookId), body.title())));
    }

    @DeleteMapping("/{notebookId}")
    @Operation(
            summary = "Delete a notebook with its topics and concepts",
            description = "Another user's notebook answers 404, like a missing one.")
    ResponseEntity<Void> delete(
            @PathVariable UUID notebookId, @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user) {
        deleteNotebook.delete(new DeleteNotebookCommand(user.id(), new NotebookId(notebookId)));
        return ResponseEntity.noContent().build();
    }
}
