package com.example.secondTelegramBot.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotConfiguration(String username, String token, WebhookProps webhook) {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebhookProps {
        private String externalUrl;   // https://...
        private String path;          // /tg/webhook
        private String secretToken;   // сверяем заголовок
        private String allowedUpdates; // "message,callback_query"
    }
}
