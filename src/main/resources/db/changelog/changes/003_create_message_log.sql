--liquibase formatted sql

--changeset bot:05-create-message-log
CREATE TABLE IF NOT EXISTS message_log (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    user_id BIGINT,
    direction VARCHAR(10) NOT NULL CHECK (direction IN ('IN', 'OUT')),
    text TEXT,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_message_log_chat_id ON message_log (chat_id);
CREATE INDEX idx_message_log_user_id ON message_log (user_id);
CREATE INDEX idx_message_log_timestamp ON message_log (timestamp);