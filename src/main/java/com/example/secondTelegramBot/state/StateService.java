package com.example.secondTelegramBot.state;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory FSM: хранит состояние по chatId.
 * На уроке 4 заменим на Postgres + JPA.
 */
@Service
public class StateService {

    private final Map<Long, UserState> storage = new ConcurrentHashMap<>();

    public UserState get(Long chatId) {
        return storage.getOrDefault(chatId, UserState.idle());
    }

    public void set(Long chatId, UserState state) {
        storage.put(chatId, state);
    }

    public void reset(Long chatId) {
        storage.put(chatId, UserState.idle());
    }
}

