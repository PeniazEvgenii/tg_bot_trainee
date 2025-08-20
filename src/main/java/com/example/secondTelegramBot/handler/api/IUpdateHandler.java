package com.example.secondTelegramBot.handler.api;

import com.example.secondTelegramBot.handler.type.EHandlerType;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Базовый контракт для всех обработчиков.
 * getType() — к какому «типу» апдейтов относится (команда, коллбек, сообщение).
 * canHandle() — точная фильтрация, чтобы не хватать чужие апдейты.
 * handle() — действие.
 */
public interface IUpdateHandler {
    EHandlerType getType();
    boolean canHandle(Update update);
    void handle(Update update);
}
