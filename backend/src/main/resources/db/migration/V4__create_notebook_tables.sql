-- Notebooks module (HU-06). owner_id has no FK to users: each module owns its tables (ADR-0010);
-- HU-04 cleans up through UserDeleted.
CREATE TABLE notebooks
(
    id         UUID PRIMARY KEY,
    owner_id   UUID                     NOT NULL,
    title      VARCHAR(150)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX notebooks_owner_updated_idx ON notebooks (owner_id, updated_at DESC);

CREATE TABLE topics
(
    id          UUID PRIMARY KEY,
    notebook_id UUID         NOT NULL REFERENCES notebooks (id) ON DELETE CASCADE,
    position    INT          NOT NULL CHECK (position >= 0),
    title       VARCHAR(150) NOT NULL
);
CREATE INDEX topics_notebook_id_idx ON topics (notebook_id);

CREATE TABLE concepts
(
    id       UUID PRIMARY KEY,
    topic_id UUID         NOT NULL REFERENCES topics (id) ON DELETE CASCADE,
    position INT          NOT NULL CHECK (position >= 0),
    title    VARCHAR(150) NOT NULL,
    text     TEXT         NOT NULL
);
CREATE INDEX concepts_topic_id_idx ON concepts (topic_id);
