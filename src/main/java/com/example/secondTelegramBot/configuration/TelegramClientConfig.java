package com.example.secondTelegramBot.configuration;

import com.example.secondTelegramBot.dispatcher.UpdateDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@RequiredArgsConstructor
public class TelegramClientConfig {
    private final BotConfiguration botConfiguration;

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botConfiguration.token());
    }

    @Bean
    public LongPollingSingleThreadUpdateConsumer myBot(UpdateDispatcher dispatcher) {
        return new LongPollingSingleThreadUpdateConsumer() {
            @Override
            public void consume(Update update) {
                dispatcher.dispatch(update);
            }
        };
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication(
            LongPollingSingleThreadUpdateConsumer consumer
    ) throws Exception {
        TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        app.registerBot(botConfiguration.token(), consumer);
        return app;
    }



}
