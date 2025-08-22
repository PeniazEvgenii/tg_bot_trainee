package com.example.secondTelegramBot.handler;

import com.example.secondTelegramBot.feign.Weather;
import com.example.secondTelegramBot.feign.WeatherService;
import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.keyboard.KeyboardFactory;
import com.example.secondTelegramBot.sender.SenderService;
import com.example.secondTelegramBot.service.ChatStateService;
import com.example.secondTelegramBot.state.BotState;
import com.example.secondTelegramBot.state.StateService;
import com.example.secondTelegramBot.state.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Order(3)
@Component
@RequiredArgsConstructor
public class MenuCallbackHandler implements IUpdateHandler {
    private final SenderService senderService;
    private final StateService stateService;
    private final WeatherService weatherService;
    private final ChatStateService chatStateService;


    @Override
    public EHandlerType getType() {
        return EHandlerType.CALLBACK;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasCallbackQuery();
    }

    @Override
    public void handle(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        String callbackQueryId = callbackQuery.getId();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        switch (data) {
            case "TIME_NOW" -> {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Minsk")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
                // можно показать notification (toast)
                senderService.answerCallback(callbackQueryId, "Время обновлено ✅", false);

                // редактируем сообщение с меню (оставим кнопки)
                senderService.editMessage(chatId, messageId, "⏰ Сейчас: " + zonedDateTime, KeyboardFactory.mainInlineMenu());
            }
            case "SURVEY" -> {
                // переходим в состояние "опрос"
                chatStateService.setState(chatId, BotState.WAITING_FOR_NAME);

                stateService.set(chatId, UserState.builder().state(BotState.WAITING_FOR_NAME).build());
                senderService.answerCallback(callbackQueryId, "Введите имя сообщением 👇", false);
                //     senderService.editMessage(chatId, messageId, "Напишите своё имя сообщением:", null);

                senderService.sendMessage(chatId, "Напишите ваше имя", KeyboardFactory.exitFromMode());
            }
            
            case "WEATHER_STUB" -> {
                Weather weather = weatherService.getWeather();
                String formatCallback = String.format("Погода в %sе %.1f °C", weather.getLocation().getName(), weather.getCurrent().getTemperature());
                String formatMessage = String.format("Погода в %s: температура %.1f°C, влажность %.0f%%. Время обновления погоды: %s",
                        weather.getLocation().getName(),
                        weather.getCurrent().getTemperature(),
                        weather.getCurrent().getHumidity(),
                        weather.getLocation().getLocaltime());

                senderService.answerCallback(callbackQueryId, "Погода обновлена ✅", false);
                senderService.editMessage(chatId, messageId, "⛅ " + formatCallback, KeyboardFactory.mainInlineMenu());

                // senderService.sendMessage(chatId, "WEATHER_STUB отправил просто как сообщение");
                senderService.sendMessage(chatId, "⛅ " + formatMessage);
            }

            case "CHOICE_ECHO" -> {
                //переходим в состояние ECHO_MODE
                chatStateService.setState(chatId, BotState.ECHO_MODE);
                stateService.set(chatId, UserState.builder().state(BotState.ECHO_MODE).build());

                senderService.answerCallback(callbackQueryId, "Переход в режим эхо", false);

                senderService.sendMessage(chatId, "Напишите любое сообщение и получите его в ответ!", KeyboardFactory.exitFromMode());
            }

            case "AI" -> {
                chatStateService.setState(chatId, BotState.AI_MODE);

                senderService.answerCallback(callbackQueryId, "Переход к общению с ИИ", false);

                senderService.sendMessage(chatId, "Задайте свой вопрос ИИ!", KeyboardFactory.exitFromMode());
            }

            case "REFRESH_MENU" -> {
                senderService.answerCallback(callbackQueryId, "Меню перемешано ⏳", false);

                senderService.editMessage(chatId, messageId, "Меню обновлено", KeyboardFactory.mainInlineMenuShuffle());
            }

            default -> senderService.answerCallback(callbackQueryId, "Неизвестное действие", true);
        }
    }
}

