package com.podcuaderno.notebooks.application.service;

import com.podcuaderno.notebooks.application.port.in.NotebookSummary;
import com.podcuaderno.notebooks.application.port.out.NotebookRepository;
import com.podcuaderno.notebooks.domain.model.Notebook;
import com.podcuaderno.notebooks.domain.model.NotebookId;
import com.podcuaderno.shared.domain.UserId;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** In-memory fake of {@link NotebookRepository} for service tests. */
class InMemoryNotebooks implements NotebookRepository {

    private final Map<NotebookId, Notebook> notebooks = new LinkedHashMap<>();

    @Override
    public Optional<Notebook> findById(NotebookId id) {
        return Optional.ofNullable(notebooks.get(id));
    }

    @Override
    public List<NotebookSummary> findSummariesByOwner(UserId owner) {
        return notebooks.values().stream()
                .filter(notebook -> notebook.isOwnedBy(owner))
                .sorted(Comparator.comparing(Notebook::updatedAt).reversed())
                .map(NotebookSummary::of)
                .toList();
    }

    @Override
    public void save(Notebook notebook) {
        notebooks.put(notebook.id(), notebook);
    }

    @Override
    public void delete(NotebookId id) {
        notebooks.remove(id);
    }

    int count() {
        return notebooks.size();
    }
}
