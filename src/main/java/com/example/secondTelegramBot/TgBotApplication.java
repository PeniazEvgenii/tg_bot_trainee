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

@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
@RequiredArgsConstructor
public class TgBotApplication {


    public static void main(String[] args) {
//        Dotenv dotenv = Dotenv.load();
//        dotenv.entries().forEach(entry -> {
//            if (System.getProperty(entry.getKey()) == null && System.getenv(entry.getKey()) == null) {
//                System.setProperty(entry.getKey(), entry.getValue());
//            }
//        });


        SpringApplication.run(TgBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner checkConfig(BotConfiguration botConfig) {
        return args -> {
            System.out.println("Username: " + botConfig.username());
            System.out.println("Token: " + botConfig.token());
        };
    }
}
