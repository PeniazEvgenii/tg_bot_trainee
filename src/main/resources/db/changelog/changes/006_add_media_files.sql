--liquibase formatted 001_database.sql

--changeset bot:006media_files
CREATE TABLE media_files (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    file_id TEXT NOT NULL,
    file_unique_id TEXT,
    file_type VARCHAR(50), -- photo, document, audio, voice
    file_name TEXT,       -- для документов/аудио
    mime_type VARCHAR(50),
    file_size INT,
    file_path TEXT,
    local_path TEXT,
    uploaded_at TIMESTAMP DEFAULT now()
);
