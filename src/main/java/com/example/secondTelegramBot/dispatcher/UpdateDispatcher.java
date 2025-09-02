package com.example.secondTelegramBot.dispatcher;

import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.repository.entity.en.MessageDirection;
import com.example.secondTelegramBot.service.MessageLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Диспетчер: определяет тип апдейта, выбирает соответствующие хендлеры,
 * вызывает первый, который "подходит".
 * Порядок внутри типов управляется @Order на классах хендлеров.
 */

@Component
@Slf4j
public class UpdateDispatcher {
    private final Map<EHandlerType, List<IUpdateHandler>> handlers;
    private final MessageLogService logService;

    public UpdateDispatcher(List<IUpdateHandler> listHandler, MessageLogService logService) {
        this.handlers = listHandler.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .collect(Collectors.groupingBy(IUpdateHandler::getType));
        this.logService = logService;
    }

    public void dispatch(Update update) {
        EHandlerType handlerType = determineType(update);
       // logService.logMessage(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), MessageDirection.IN, update.getMessage().getText());


        List<IUpdateHandler> handlerList = handlers.getOrDefault(handlerType, List.of());

        for (IUpdateHandler handler : handlerList) {
            try {
                if (handler.canHandle(update)) {
                    handler.handle(update);
                    return;
                }
            } catch (Exception e) {
                log.error("Handler error: {}", handler.getClass().getSimpleName(), e);

            }
        }
        log.warn("No handler matched. Type={}, update={}", handlerType, update);
    }


    private EHandlerType determineType(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()) {
            return EHandlerType.COMMAND;
        } else if(update.hasCallbackQuery()) {
            return EHandlerType.CALLBACK;
        } else if(update.hasMessage() && hasMedia(update.getMessage())) {
            return EHandlerType.MEDIA;
        } else if(update.hasMessage()) {
            return EHandlerType.MESSAGE;
        }
        return EHandlerType.OTHER;
    }

    private boolean hasMedia(Message message) {
        return message.hasAudio() || message.hasDocument() || message.hasPhoto() || message.hasVoice();
    }
}
