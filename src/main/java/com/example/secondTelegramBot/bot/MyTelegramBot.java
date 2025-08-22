package com.example.secondTelegramBot.bot;

import com.example.secondTelegramBot.configuration.BotConfiguration;
import com.example.secondTelegramBot.dispatcher.UpdateDispatcher;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class MyTelegramBot {
    private final BotConfiguration botConfiguration;
    private final UpdateDispatcher dispatcher;

    public MyTelegramBot(BotConfiguration botConfiguration, UpdateDispatcher dispatcher) {

        this.botConfiguration = botConfiguration;
        this.dispatcher = dispatcher;
    }

    public void onUpdateReceived(Update update) {
        dispatcher.dispatch(update);
    }

    public String getBotUsername() {
        return botConfiguration.username();
    }

    @PostConstruct
    public void initCommands() {
        try {
            List<BotCommand> commands = Arrays.asList(
                    new BotCommand("/start", "Начать работу"),
                    new BotCommand("/help", "Помощь")
            );
          //  execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
