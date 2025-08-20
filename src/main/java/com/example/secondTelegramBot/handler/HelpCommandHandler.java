package com.example.secondTelegramBot.handler;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.sender.SenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(2)
@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements IUpdateHandler {
    private static final String TEXT_HELP = "/help";

    private final SenderService senderService;


    @Override
    public EHandlerType getType() {
        return EHandlerType.COMMAND;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().isCommand()
                && TEXT_HELP.equals(update.getMessage().getText());
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        senderService.sendMessage(
               chatId,
                """
                     Доступные команды:
     /start — показать меню
     /help — помощь
    \s
     Кнопки:
     ⏰ Показать время — пришлю текущее время
     👋 Привет (установить имя) — спрошу имя и запомню
     ⛅ Погода — пока не работает
                    \s"""
        );
    }
}
