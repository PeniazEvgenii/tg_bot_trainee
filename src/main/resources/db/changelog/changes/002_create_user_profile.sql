--liquibase formatted sql

--changeset mentor:002
CREATE TABLE app.user_profile (
    chat_id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    city VARCHAR(50),
    updated_at TIMESTAMP DEFAULT now()
);