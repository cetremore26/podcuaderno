package com.podcuaderno.notebooks.adapter.out.persistence;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.UserId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Runs inside the transaction of the calling application service. */
@Component
class NotebookPersistenceAdapter implements NotebookRepository {

    private final NotebookJpaRepository jpa;

    NotebookPersistenceAdapter(NotebookJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Notebook> findById(NotebookId id) {
        return jpa.findById(id.value()).map(NotebookMapper::toDomain);
    }

    @Override
    public List<NotebookSummary> findSummariesByOwner(UserId owner) {
        return jpa.findByOwnerIdOrderByUpdatedAtDesc(owner.value()).stream()
                .map(NotebookMapper::toSummary)
                .toList();
    }

    @Override
    public void save(Notebook notebook) {
        // Topic and concept changes are synchronised when the editor arrives (HU-11).
        jpa.findById(notebook.id().value())
                .ifPresentOrElse(
                        entity -> entity.rename(notebook.title().value(), notebook.updatedAt()),
                        () -> jpa.save(NotebookMapper.toNewEntity(notebook)));
    }

    @Override
    public void delete(NotebookId id) {
        jpa.deleteNotebook(id.value());
    }
}
