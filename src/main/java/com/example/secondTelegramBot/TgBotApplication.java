package com.example.secondTelegramBot;

import com.example.secondTelegramBot.configuration.BotConfiguration;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
@RequiredArgsConstructor
public class TgBotApplication {


    public static void main(String[] args) {
        SpringApplication.run(TgBotApplication.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner checkConfig(BotConfiguration botConfig) {
        return args -> {
            System.out.println("Username: " + botConfig.username());
            System.out.println("Token: " + botConfig.token());
        };
    }
}
