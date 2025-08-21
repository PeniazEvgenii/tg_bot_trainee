package com.example.secondTelegramBot.keyboard;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Утилита, чтобы собирать клавиатуры в builder-стиле и не дублировать код.
 */
@UtilityClass
public class KeyboardFactory {

    // --- Inline меню (две кнопки в столбик + одна внизу) ---
    public static InlineKeyboardMarkup mainInlineMenu() {
        InlineKeyboardButton time = inlineKeyboardButtonBuild("⏰ Показать время", "TIME_NOW");
//                .text("⏰ Показать время")
//                .callbackData("TIME_NOW")
//                .build();

        InlineKeyboardButton survey = inlineKeyboardButtonBuild("\uD83D\uDCDD Пройти опрос", "SURVEY");

        InlineKeyboardButton echo = InlineKeyboardButton.builder()
                .text("\uD83C\uDFAF Echo (режим)")
                .callbackData("CHOICE_ECHO")
                .build();

        InlineKeyboardButton hello = InlineKeyboardButton.builder()
                .text("👋 Привет (установить имя)")
                .callbackData("SET_NAME")
                .build();

        InlineKeyboardButton ai = InlineKeyboardButton.builder()
                .text("\uD83E\uDD16 Пообщаться с AI")
                .callbackData("AI")
                .build();

        InlineKeyboardButton weather = InlineKeyboardButton.builder()
                .text("⛅ Погода")
                .callbackData("WEATHER_STUB")
                .build();

        InlineKeyboardButton refreshMenu = InlineKeyboardButton.builder()
                .callbackData("REFRESH_MENU")
                .text("♻️ Обновить меню")
                .build();

        InlineKeyboardRow row1 = new InlineKeyboardRow(List.of(time, weather));
        InlineKeyboardRow row2 = new InlineKeyboardRow(List.of(ai));
        InlineKeyboardRow row3 = new InlineKeyboardRow(List.of(survey));
        InlineKeyboardRow row4 = new InlineKeyboardRow(List.of(echo));
        InlineKeyboardRow row5 = new InlineKeyboardRow(List.of(refreshMenu));

        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(row1, row2, row3, row4, row5))
                .build();
    }

    // --- Reply клавиатура (быстрые действия) ---
    public static ReplyKeyboardMarkup quickReplyKeyboard() {
        KeyboardRow row1 = new KeyboardRow(List.of(
                KeyboardButton.builder().text("Меню").build()
        ));

        KeyboardRow row2 = new KeyboardRow(List.of(
           KeyboardButton.builder().text("Помощь").build(),
           KeyboardButton.builder().text("Скрыть клавиатуру").build()
        ));

        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(List.of(row1, row2))
                .build();
    }


    public static ReplyKeyboardRemove hideReplyKeyboard() {
        return ReplyKeyboardRemove.builder()
                .removeKeyboard(true)
                .build();
    }

    public ReplyKeyboardMarkup exitFromMode() {
        KeyboardRow row = new KeyboardRow(List.of(
                KeyboardButton.builder().text("Выход из режима").build()
        ));

        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    private static InlineKeyboardButton inlineKeyboardButtonBuild(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    public static InlineKeyboardMarkup mainInlineMenuShuffle() {
        InlineKeyboardButton time = inlineKeyboardButtonBuild("⏰ Показать время", "TIME_NOW");
        InlineKeyboardButton echo = inlineKeyboardButtonBuild("\uD83C\uDFAF Echo (режим)", "CHOICE_ECHO");
        InlineKeyboardButton hello = inlineKeyboardButtonBuild("👋 Привет (установить имя)", "SET_NAME");
        InlineKeyboardButton weather = inlineKeyboardButtonBuild("⛅ Погода","WEATHER_STUB");
        InlineKeyboardButton refreshMenu = inlineKeyboardButtonBuild("♻️ Обновить меню","REFRESH_MENU");
        InlineKeyboardButton survey = inlineKeyboardButtonBuild("\uD83D\uDCDD Пройти опрос", "SURVEY");
        InlineKeyboardButton ai = inlineKeyboardButtonBuild("\uD83E\uDD16 Пообщаться с AI", "AI");

        List<List<InlineKeyboardButton>> lists = new ArrayList<>(List.of(
                List.of(time, weather),
                List.of(survey),
                List.of(echo),
                List.of(ai),
                List.of(refreshMenu)
        ));

        InlineKeyboardRow row1 = new InlineKeyboardRow(List.of(time, weather));
        InlineKeyboardRow row2 = new InlineKeyboardRow(List.of(ai));
        InlineKeyboardRow row3 = new InlineKeyboardRow(List.of(survey));
        InlineKeyboardRow row4 = new InlineKeyboardRow(List.of(echo));
        InlineKeyboardRow row5 = new InlineKeyboardRow(List.of(refreshMenu));

        List<InlineKeyboardRow> lists2 = new ArrayList<>(List.of(
                row1, row2, row3, row4, row5
        ));

        Collections.shuffle(lists);
        Collections.shuffle(lists2);

        return InlineKeyboardMarkup.builder()
                .keyboard(lists2)
                .build();
    }

}
