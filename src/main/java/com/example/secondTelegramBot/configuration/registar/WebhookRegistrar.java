package com.example.secondTelegramBot.configuration.registar;

import com.example.secondTelegramBot.configuration.BotConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookRegistrar implements ApplicationRunner {

    private final TelegramClient client;
    private final BotConfiguration props;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!"webhook".equalsIgnoreCase(props.mode())) {
            log.info("WebhookRegistrar skipped (mode != webhook)");
            return;
        }

        String base = props.webhook().getExternalUrl();
        String path = props.webhook().getPath(); // like /tg/webhook
        if (base == null || base.isBlank() || path == null || path.isBlank()) {
            throw new IllegalStateException("Missing webhook URL config");
        }

        String fullUrl = base.endsWith("/") ? base.substring(0, base.length()-1) + path : base + path;

        List<String> allowed = Arrays.stream(
                        Optional.ofNullable(props.webhook().getAllowedUpdates()).orElse("message,callback_query").split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();

        var setWebhook = SetWebhook.builder()
                .url(fullUrl)
                .secretToken(props.webhook().getSecretToken())
                .allowedUpdates(allowed)
                .build();

        try {
            client.execute(setWebhook);
            log.info("Webhook registered at {}", fullUrl);
        } catch (TelegramApiException e) {
            log.error("Failed to set webhook {}: {}", fullUrl, e.getMessage(), e);
            throw e;
        }
    }
}
