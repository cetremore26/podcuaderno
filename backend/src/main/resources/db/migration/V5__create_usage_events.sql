-- Metrics module. Only type and character count, never content (02 §7, CU-05). No FK to users (ADR-0010).
CREATE TABLE usage_events
(
    id          UUID PRIMARY KEY,
    user_id     UUID                     NOT NULL,
    type        VARCHAR(40)              NOT NULL,
    characters  INT                      NOT NULL CHECK (characters >= 0),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX usage_events_occurred_at_idx ON usage_events (occurred_at);
CREATE INDEX usage_events_user_id_idx ON usage_events (user_id);
