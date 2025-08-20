package com.example.secondTelegramBot.handler;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.keyboard.KeyboardFactory;
import com.example.secondTelegramBot.sender.SenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(1)
@RequiredArgsConstructor
@Component
public class StartCommandHandler implements IUpdateHandler {
    private static final String TEXT_START = "/start";

    private final SenderService senderService;

    @Override
    public EHandlerType getType() {
        return EHandlerType.COMMAND;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().isCommand()
                && TEXT_START.equalsIgnoreCase(update.getMessage().getText().trim());
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        senderService.sendMessage(
                chatId,
                "Привет! Я бот 👋\nВыбери действие:",
                KeyboardFactory.mainInlineMenu()
        );

        // Дополнительно выведем reply-клавиатуру (например быстрые действия)
        senderService.sendMessage(
                chatId,
                "Быстрые действия на клавиатуре ↓",
                KeyboardFactory.quickReplyKeyboard()
        );
    }
}

