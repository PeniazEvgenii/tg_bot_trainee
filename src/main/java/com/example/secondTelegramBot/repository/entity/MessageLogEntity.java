package com.example.secondTelegramBot.repository.entity;

import com.example.secondTelegramBot.repository.entity.en.MessageDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message_log")
public class MessageLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private MessageDirection direction;  // IN (от пользователя) / OUT (от бота)

    @Column(columnDefinition = "TEXT")
    private String text;             // Текст сообщения

    private LocalDateTime timestamp; // Когда сообщение получено/отправлено
}
