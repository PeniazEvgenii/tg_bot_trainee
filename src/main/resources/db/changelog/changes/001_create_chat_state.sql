--liquibase formatted 001_database.sql

--changeset mentor:001
CREATE TABLE app.chat_state (
    chat_id BIGINT PRIMARY KEY,
    current_state VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP DEFAULT now()
);