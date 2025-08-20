package com.example.secondTelegramBot.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Сущность для хранения состояния диалога пользователя.
 * Например: MENU, AWAITING_NAME, AWAITING_AGE и т.д.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat_state")
public class ChatStateEntity {

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "current_state", nullable = false, length = 50)
    private String currentState;

    @Column(name = "updated_at")
    private Instant updatedAt;
}

