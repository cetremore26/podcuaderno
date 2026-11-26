package com.podcuaderno.notebooks.domain.model;

import com.podcuaderno.shared.domain.UserId;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Aggregate root: a notebook of one student, with its topics and concepts (HU-06). */
public final class Notebook {

    private final NotebookId id;
    private final UserId owner;
    private final List<Topic> topics;
    private final Instant createdAt;
    private Title title;
    private Instant updatedAt;

    private Notebook(
            NotebookId id, UserId owner, Title title, List<Topic> topics, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.owner = Objects.requireNonNull(owner, "owner must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.topics = List.copyOf(Objects.requireNonNull(topics, "topics must not be null"));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /** A new, empty notebook. The structure imported from notes arrives with HU-07. */
    public static Notebook create(NotebookId id, UserId owner, Title title, Instant now) {
        return new Notebook(id, owner, title, List.of(), now, now);
    }

    public static Notebook restore(
            NotebookId id, UserId owner, Title title, List<Topic> topics, Instant createdAt, Instant updatedAt) {
        return new Notebook(id, owner, title, topics, createdAt, updatedAt);
    }

    /** Renaming to the same title is not a modification and keeps {@link #updatedAt()}. */
    public void rename(Title newTitle, Instant now) {
        Objects.requireNonNull(newTitle, "newTitle must not be null");
        if (!title.equals(newTitle)) {
            title = newTitle;
            updatedAt = Objects.requireNonNull(now, "now must not be null");
        }
    }

    public boolean isOwnedBy(UserId userId) {
        return owner.equals(userId);
    }

    public NotebookId id() {
        return id;
    }

    public UserId owner() {
        return owner;
    }

    public Title title() {
        return title;
    }

    public List<Topic> topics() {
        return topics;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
