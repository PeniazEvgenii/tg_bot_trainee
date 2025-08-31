package com.example.secondTelegramBot.handler.mediaHandller;

import com.example.secondTelegramBot.handler.api.IUpdateHandler;
import com.example.secondTelegramBot.handler.type.EHandlerType;
import com.example.secondTelegramBot.handler.type.EMediaType;
import com.example.secondTelegramBot.sender.SenderService;
import com.example.secondTelegramBot.service.MediaService;
import com.example.secondTelegramBot.service.dto.MediaFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PhotoHandler implements IUpdateHandler {
    private final MediaService mediaService;
    private final SenderService senderService;

    @Override
    public EHandlerType getType() {
        return EHandlerType.MEDIA;
    }

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasPhoto();
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        List<PhotoSize> photos = message.getPhoto();
        PhotoSize photo = photos.getLast();

        MediaFileDto mediaFileDto = MediaFileDto.builder()
                .chatId(chatId)
                .fileId(photo.getFileId())
                .fileUniqueId(photo.getFileUniqueId())
                .fileType(EMediaType.PHOTO)
                .fileSize(photo.getFileSize())
                .build();

        mediaService.save(mediaFileDto);
        senderService.sendMessage(chatId, "Фото загружено");
    }
}
