package com.example.secondTelegramBot.storage;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserDataCache {

    private final ConcurrentHashMap<Long, String> names = new ConcurrentHashMap<>();

    public void saveUsername(Long chatId, String username) {
        names.put(chatId, username);
    }

    public String getUserName(Long chatId) {
        return names.getOrDefault(chatId, "Гость");
    }
}
