--liquibase formatted 001_database.sql

--changeset bot:007-create-schema-log
ALTER TABLE media_files SET SCHEMA app;
