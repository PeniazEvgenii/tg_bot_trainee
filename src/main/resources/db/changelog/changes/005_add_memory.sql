--liquibase formatted sql

--changeset bot:005memory-log
CREATE TABLE SPRING_AI_CHAT_MEMORY (
    id SERIAL PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversation_id ON SPRING_AI_CHAT_MEMORY (conversation_id);
