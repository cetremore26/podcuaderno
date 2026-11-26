-- Identity module (HU-01, HU-05). English names (ADR-0005).
CREATE TABLE users
(
    id                UUID PRIMARY KEY,
    email             VARCHAR(254)             NOT NULL,
    password_hash     VARCHAR(255)             NOT NULL,
    status            VARCHAR(30)              NOT NULL CHECK (status IN ('PENDING_VERIFICATION', 'ACTIVE')),
    role              VARCHAR(20)              NOT NULL CHECK (role IN ('STUDENT', 'ADMIN')),
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    email_verified_at TIMESTAMP WITH TIME ZONE
);
CREATE UNIQUE INDEX users_email_key ON users (email);

CREATE TABLE consents
(
    id             UUID PRIMARY KEY,
    user_id        UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    policy_version VARCHAR(20)              NOT NULL,
    accepted_at    TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX consents_user_id_idx ON consents (user_id);

CREATE TABLE email_verification_tokens
(
    id         UUID PRIMARY KEY,
    user_id    UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash VARCHAR(64)              NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at    TIMESTAMP WITH TIME ZONE
);
CREATE INDEX email_verification_tokens_user_idx ON email_verification_tokens (user_id, created_at DESC);
