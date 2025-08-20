--liquibase formatted sql

--changeset bot:004-create-schema-log
ALTER TABLE message_log SET SCHEMA app;
