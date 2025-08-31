package com.example.secondTelegramBot.service.dto;

import com.example.secondTelegramBot.handler.type.EMediaType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaFileDto {
    private Long id;
    private Long chatId;
    private String fileId;
    private String fileUniqueId;
    private EMediaType fileType;   // photo, document, audio, voice
    private String fileName;   // для документов/аудио
    private String mimeType;   // может быть null
    private Integer fileSize;  // может быть null

    private String filePath;   // путь у Telegram
    private String localPath;  // локально скачанный файл
}
