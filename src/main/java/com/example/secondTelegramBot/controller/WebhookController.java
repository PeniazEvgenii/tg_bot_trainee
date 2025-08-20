package com.example.secondTelegramBot.controller;

import com.example.secondTelegramBot.dispatcher.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final UpdateDispatcher updateDispatcher;

    public ResponseEntity<Void> onUpdateReceived(@RequestBody Update update) {
        updateDispatcher.dispatch(update);
        return ResponseEntity.ok().build();
    }
}
