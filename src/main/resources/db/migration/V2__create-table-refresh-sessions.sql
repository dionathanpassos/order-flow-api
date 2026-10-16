CREATE TABLE refresh_sessions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    last_used_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,

    CONSTRAINT uk_refresh_sessions_token_hash UNIQUE (token_hash),

    CONSTRAINT fk_refresh_sessions_user FOREIGN KEY (user_id) REFERENCES tb_users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_sessions_hash ON refresh_sessions (token_hash);