package com.example.secondTelegramBot.handler;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.keyboard.KeyboardFactory;
import com.example.secondTelegramBot.repository.entity.UserProfileEntity;
import com.example.secondTelegramBot.sender.SenderService;
import com.example.secondTelegramBot.service.ChatAiService;
import com.example.secondTelegramBot.service.ChatStateService;
import com.example.secondTelegramBot.service.UserProfileService;
import com.example.secondTelegramBot.state.BotState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Slf4j
@Order(5)
@Component
@RequiredArgsConstructor
public class FsmHandler implements IUpdateHandler {
    private final ChatStateService chatStateService;
    private final SenderService senderService;
    private final UserProfileService userProfileService;
    private final ChatAiService chatAiService;

    @Override
    public EHandlerType getType() {
        return EHandlerType.MESSAGE;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && !update.getMessage().getText().equalsIgnoreCase("Выход из режима")
                && !chatStateService.getState(update.getMessage().getChatId()).equals(BotState.IDLE);
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText().trim();

        log.info("Пользователь написал: {}", text);
        BotState currentState = chatStateService.getState(chatId);

        switch (currentState) {
            case ECHO_MODE -> senderService.sendMessage(chatId, text.toUpperCase(), KeyboardFactory.exitFromMode());
            case WAITING_FOR_NAME -> {
                UserProfileEntity userProfileEntity = new UserProfileEntity();
                userProfileEntity.setName(text);
                userProfileEntity.setChatId(chatId);
                userProfileService.saveProfile(userProfileEntity);

                chatStateService.setState(chatId, BotState.WAITING_FOR_AGE);
                senderService.sendMessage(chatId, "Введите ваш возраст", KeyboardFactory.exitFromMode());
            }
            case WAITING_FOR_AGE -> {
                UserProfileEntity userProfileEntity = userProfileService.getProfile(chatId).orElseThrow();
                try {
                    int age = Integer.parseInt(text);
                    userProfileEntity.setAge(age);
                    userProfileService.saveProfile(userProfileEntity);

                    chatStateService.setState(chatId, BotState.WAITING_FOR_CITY);
                    senderService.sendMessage(chatId, "Введите ваш город", KeyboardFactory.exitFromMode());
                } catch (NumberFormatException e) {
                    senderService.sendMessage(chatId, "Вы ввели не число. Введите ваш возраст", KeyboardFactory.exitFromMode());
                }
            }
            case WAITING_FOR_CITY -> {
                UserProfileEntity userProfileEntity = userProfileService.getProfile(chatId).orElseThrow();
                userProfileEntity.setCity(text);
                String result = "✅ Спасибо! Ваша анкета сохранена:\n" +
                        "Имя: " + userProfileEntity.getName() + "\n" +
                        "Возраст: " + userProfileEntity.getAge() + "\n" +
                        "Город: " + userProfileEntity.getCity();
                userProfileService.saveProfile(userProfileEntity);

                chatStateService.clearState(chatId);

                senderService.sendMessage(chatId, result);
                senderService.sendMessage(chatId, "Что дальше?", KeyboardFactory.mainInlineMenu());
            }
            case AI_MODE -> {
                SendChatAction chatAction = SendChatAction.builder()
                        .chatId(chatId)
                        .action("typing")
                        .build();
                senderService.executeGeneric(chatAction);
               // String answer = chatAiService.processOneMessage(text);
                String ask = chatAiService.ask(chatId, text);

                senderService.sendMessage(chatId, ask, KeyboardFactory.exitFromMode());
            }
        }
    }
}
