package com.example.secondTelegramBot.service;

import com.example.secondTelegramBot.repository.MessageLogRepository;
import com.example.secondTelegramBot.repository.entity.MessageLogEntity;
import com.example.secondTelegramBot.repository.entity.en.MessageDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageLogService {
    private final MessageLogRepository repository;

    public void logMessage(Long chatId, Long userId, MessageDirection direction, String text) {
        MessageLogEntity entity = MessageLogEntity.builder()
                .chatId(chatId)
                .userId(userId)
                .direction(direction)
                .text(text)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(entity);
    }
}
