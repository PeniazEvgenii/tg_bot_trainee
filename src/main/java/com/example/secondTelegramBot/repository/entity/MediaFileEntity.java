package com.example.secondTelegramBot.repository.entity;

import com.example.secondTelegramBot.handler.type.EMediaType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "media_files")
@EntityListeners(AuditingEntityListener.class)
public class MediaFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String fileId;
    private String fileUniqueId;
    @Enumerated(EnumType.STRING)
    private EMediaType fileType;   // photo, document, audio, voice
    private String fileName;   // для документов/аудио
    private String mimeType;   // может быть null
    private Integer fileSize;  // может быть null

    private String filePath;   // путь у Telegram
    private String localPath;  // локально скачанный файл

    @LastModifiedDate
    private LocalDateTime uploadedAt;

    // геттеры/сеттеры/конструкторы через Lombok
}
