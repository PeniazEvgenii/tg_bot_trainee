package com.example.secondTelegramBot.handler.mediaHandller;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.handler.type.EMediaType;
import com.example.secondTelegramBot.sender.SenderService;
import com.example.secondTelegramBot.service.MediaService;
import com.example.secondTelegramBot.service.dto.MediaFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
public class DocumentHandler implements IUpdateHandler {
    private final MediaService mediaService;
    private final SenderService senderService;

    @Override
    public EHandlerType getType() {
        return EHandlerType.MEDIA;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasDocument();
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Document document = message.getDocument();
        MediaFileDto mediaFileDto = MediaFileDto.builder()
                .chatId(chatId)
                .fileId(document.getFileId())
                .fileUniqueId(document.getFileUniqueId())
                .fileType(EMediaType.DOCUMENT)
                .fileSize(Math.toIntExact(document.getFileSize()))
                .mimeType(document.getMimeType())
                .fileName(document.getFileName())
                .build();
        mediaService.save(mediaFileDto);
        senderService.sendMessage(chatId, "Документ загружен");
    }
}
