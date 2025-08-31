package com.example.secondTelegramBot.service;

import com.example.secondTelegramBot.configuration.BotConfiguration;
import com.example.secondTelegramBot.handler.type.EMediaType;
import com.example.secondTelegramBot.repository.MediaFileRepository;
import com.example.secondTelegramBot.repository.entity.MediaFileEntity;
import com.example.secondTelegramBot.service.dto.MediaFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaService {
    private static final String BUCKET = "/download";
    private static final String TELEGRAM_URL = "https://api.telegram.org/file/bot";

    private final MediaFileRepository mediaFileRepository;
    private final BotConfiguration configuration;
    private final TelegramClient telegramClient;
    private final S3StorageService s3StorageService;

    @Transactional
    public Long save(MediaFileDto mediaFileDto) {

        GetFile getFile = GetFile.builder()
                .fileId(mediaFileDto.getFileId())
                .build();

        try {
            File fileTg = telegramClient.execute(getFile);

            String fileUrl = TELEGRAM_URL + configuration.token() + "/" + fileTg.getFilePath();

            String fileName = Instant.now().toEpochMilli() + "_" + (mediaFileDto.getFileName() != null
                    ? mediaFileDto.getFileName()
                    : mediaFileDto.getFileUniqueId() + "." + (mediaFileDto.getFileType().equals(EMediaType.PHOTO) ? "jpg" : mediaFileDto.getFileType()));

            Path fullPath = Path.of(BUCKET, fileName);
            Files.createDirectories(fullPath.getParent());

//            try (BufferedInputStream inputStream = new BufferedInputStream();
//                 OutputStream outputStream = Files.newOutputStream(fullPath)
//            ) {
//
//                inputStream.transferTo(outputStream);
//                Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
//            }
            HttpClient build = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .header("User-Agent", "MyBot/1.0")
                    .uri(URI.create(fileUrl))
                    .build();

            HttpResponse<InputStream> send = build.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            long contentLength = send.headers().firstValue(HttpHeaders.CONTENT_LENGTH).map(Long::parseLong).orElse(-1L);
            String contentType = send.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse("application/octet-stream");

            String keyFromS3 = s3StorageService.uploadStream(send.body(), contentLength, contentType, fileName);

            // Это сохраняет на мой диск
//            try (InputStream body = send.body();
//                 OutputStream outputStream = Files.newOutputStream(fullPath)
//            ) {
//                //body.transferTo(outputStream);
//                 Files.copy(body, fullPath, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }


//            try (InputStream in = new URL(fileUrl).openStream()) {
//                Files.copy(in, fullPath, StandardCopyOption.REPLACE_EXISTING);
//            }


            MediaFileEntity mediaFileEntity = MediaFileEntity.builder()
                    .chatId(mediaFileDto.getChatId())
                    .fileId(mediaFileDto.getFileId())
                    .fileUniqueId(mediaFileDto.getFileUniqueId())
                    .fileType(mediaFileDto.getFileType())
                    .mimeType(mediaFileDto.getMimeType())
                    .fileSize(mediaFileDto.getFileSize())
                    .filePath(fileTg.getFilePath())
                   // .localPath(fullPath.toString())
                    .localPath(keyFromS3)
                    .build();

            mediaFileEntity = mediaFileRepository.saveAndFlush(mediaFileEntity);
            return mediaFileEntity.getId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
