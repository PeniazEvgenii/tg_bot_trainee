package com.example.secondTelegramBot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatAiService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ChatMemoryRepository chatMemoryRepository;
    private final Map<Long, ChatMemory> memoryMap = new ConcurrentHashMap<>();

    private final Map<Long, List<Message>> messages = new ConcurrentHashMap<>();

    public ChatAiService(ChatClient chatClient, ChatMemory chatMemory, ChatMemoryRepository chatMemoryRepository) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    public String processOneMessage(String userMessage) {

        return chatClient.prompt("Ты — полезный ассистент")
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * Отправка сообщения пользователем и получение ответа ИИ
     */
    public String ask(Long chatId, String userText) {
        String conversationId = chatId.toString();
        List<Message> history = chatMemory.get(conversationId);
        if(history.isEmpty()) {
            history.add(SystemMessage.builder().text("Ты — полезный ассистент").build());
            chatMemory.add(conversationId,history);
        }


        // Advisor автоматически использует память при запросе к ChatClient
        ChatResponse response = chatClient.prompt()
                .messages(history)
                .user(userText)
                .call()
                .chatClientResponse()
                .chatResponse();

        chatMemory.add(conversationId, List.of(
                new UserMessage(userText),
                response.getResult().getOutput()
        ));

        return response.getResult().getOutput().getText();
    }

    /**
     * Получение или создание ChatMemory для пользователя
     */
    private ChatMemory getMemoryForUser(Long chatId) {
        return memoryMap.computeIfAbsent(chatId, id ->
                MessageWindowChatMemory.builder()
                        .chatMemoryRepository(chatMemoryRepository)
                        .maxMessages(10) // храним только последние 10 сообщений
                        .build()
        );
    }


}
