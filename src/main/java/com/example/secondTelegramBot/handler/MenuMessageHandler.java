package com.example.secondTelegramBot.handler;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.keyboard.KeyboardFactory;
import com.example.secondTelegramBot.sender.SenderService;
import com.example.secondTelegramBot.service.ChatStateService;
import com.example.secondTelegramBot.state.BotState;
import com.example.secondTelegramBot.state.StateService;
import com.example.secondTelegramBot.storage.UserDataCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * Обрабатывает обычные текстовые сообщения.
 * Если FSM = WAITING_FOR_NAME — сохраняем имя (в демо просто эхо) и возвращаемся в IDLE.
 * Также поддерживаем reply-кнопки: "Меню", "Помощь", "Скрыть клавиатуру".
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuMessageHandler implements IUpdateHandler {
    private final Set<String> menus = new HashSet<>(Set.of("Меню", "Помощь", "Скрыть клавиатуру"));
    private final SenderService senderService;
    private final StateService stateService;
    private final UserDataCache userDataCache;
    private final ChatStateService chatStateService;

    @Override
    public EHandlerType getType() {
        return EHandlerType.MESSAGE;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText();
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = message.getText();

        log.info("Пользователь{}, написал: {}", userDataCache.getUserName(chatId), text);

        // 1) FSM: ждём имя. Обработка сообщение с состоянием перенес в отдельный хендлер

        // 2) Обработка reply-кнопок (быстрые действия)
        switch (text) {
            case "Меню" -> senderService.sendMessage(chatId, userDataCache.getUserName(chatId) + ", Выберите действие:", KeyboardFactory.mainInlineMenu());
            case "Помощь" -> senderService.sendMessage(chatId, """
                                        Доступные команды:
                    /start — показать меню
                    /help — помощь
                    Кнопки: ⏰ время, 👋 имя, ⛅ погода
                    """);
            case "Скрыть клавиатуру" ->
                    senderService.sendMessage(chatId, "Клавиатура скрыта.", KeyboardFactory.hideReplyKeyboard());
            case "Выход из режима" -> {
                chatStateService.clearState(chatId);
                stateService.reset(chatId);
                senderService.sendMessage(chatId, "Вы вышли из режима", KeyboardFactory.quickReplyKeyboard());
            }
            default -> senderService.sendMessage(chatId, "Эхо: " + text);
        }
    }
}
