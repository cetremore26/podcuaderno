-- Refresh tokens of HU-02: only the SHA-256 of the token is stored; rotation keeps the family (ADR-0009).
CREATE TABLE refresh_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    family_id  UUID                     NOT NULL,
    token_hash VARCHAR(64)              NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    rotated_at TIMESTAMP WITH TIME ZONE,
    revoked_at TIMESTAMP WITH TIME ZONE
);
CREATE INDEX refresh_tokens_family_id_idx ON refresh_tokens (family_id);
CREATE INDEX refresh_tokens_user_id_idx ON refresh_tokens (user_id);
