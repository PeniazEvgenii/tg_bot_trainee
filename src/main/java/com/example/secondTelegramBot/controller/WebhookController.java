package com.example.secondTelegramBot.controller;

import com.example.secondTelegramBot.configuration.BotConfiguration;
import com.example.secondTelegramBot.dispatcher.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
@RequestMapping("tg/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final UpdateDispatcher dispatcher;
    private final BotConfiguration props;

    @PostMapping("/reserve")
    public ResponseEntity<Void> onUpdateReceived(@RequestBody Update update) {
        dispatcher.dispatch(update);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> onUpdate(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String headerToken,
            @RequestBody Update update) {
        log.info("Get Request on Controller Webhook from updateId: {}", update.getUpdateId());
        String expected = props.webhook().getSecretToken();
        if (expected != null && !expected.isBlank()) {
            if (headerToken == null || !expected.equals(headerToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // делегируем обработку (FSM/handlers) — обработка может выполняться синхронно или асинхронно
        dispatcher.dispatch(update);

        // Telegram ждёт 200 OK быстро
        return ResponseEntity.ok().build();
    }
}
