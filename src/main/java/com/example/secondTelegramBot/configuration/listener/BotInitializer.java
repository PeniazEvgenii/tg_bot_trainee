package com.example.secondTelegramBot.configuration.listener;

import com.example.secondTelegramBot.bot.MyTelegramBot;
import com.example.secondTelegramBot.sender.SenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {
    private final MyTelegramBot bot;
    private final TelegramBotsApi telegramBotsApi;
    private final SenderService senderService;

    private final AtomicBoolean init = new AtomicBoolean(false);

    @EventListener(ApplicationEvent.class)
    public void onApplicationReade() {
        if(!init.compareAndSet(false, true)) {
            log.info("Bot already initialized. Skipping registration.");
            return;
        }

        try {
            log.info("Registering Telegram bot...");

            telegramBotsApi.registerBot(bot);

            senderService.setBot(bot);

            log.info("Telegram bot registered and SenderService bound successfully.");
        } catch (Exception e) {
            log.error("Failed to register Telegram bot", e);
            throw new IllegalStateException("Bot registration failed", e);
        }
    }
}
