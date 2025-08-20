package com.example.secondTelegramBot.service;

import com.example.secondTelegramBot.repository.ChatStateRepository;
import com.example.secondTelegramBot.repository.entity.ChatStateEntity;
import com.example.secondTelegramBot.state.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatStateService {

    private final ChatStateRepository chatStateRepository;

    /**
     * Установить новое состояние для чата
     */
    public ChatStateEntity setState(Long chatId, BotState state) {
        ChatStateEntity entity = ChatStateEntity.builder()
                .chatId(chatId)
                .currentState(state.name())
                .updatedAt(Instant.now())
                .build();

        return chatStateRepository.save(entity);
    }

    /**
     * Получить текущее состояние чата
     */
    public BotState getState(Long chatId) {
        return chatStateRepository.findById(chatId)
                .map(st -> BotState.valueOf(st.getCurrentState()))
                .orElse(BotState.IDLE);
    }

    /**
     * Удалить состояние (например, при завершении диалога)
     */
    public void clearState(Long chatId) {
        chatStateRepository.deleteById(chatId);
    }
}