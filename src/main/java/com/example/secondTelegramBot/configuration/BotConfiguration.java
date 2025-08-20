package com.example.secondTelegramBot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotConfiguration(String username, String token) {
}
