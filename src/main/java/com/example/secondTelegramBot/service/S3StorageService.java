package com.example.secondTelegramBot.service;

import com.example.secondTelegramBot.configuration.S3Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final S3Configuration s3Configuration;

    /**
     * Загружает поток в S3. contentLength может быть -1, но лучше передавать реальный размер.
     * Возвращает s3Key.
     * Примечания:
     *      RequestBody.fromInputStream в AWS SDK v2 требует contentLength.
     *          Если ты не знаешь длину (например, поток из HttpClient без header Content-Length),
     *          то лучше сначала скачать во временный файл или получить Content-Length из HttpResponse.headers().
     *      Для очень больших файлов используй Transfer Manager (опционально) или multipart upload.
     */
    public String uploadStream(InputStream in,
                               long contentLength,
                               String contentType,
                               String filename) {
        String key = "media/" + UUID.randomUUID() + "_" + sanitize(filename);

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(s3Configuration.bucket())
                .key(key)
                .contentType(contentType)
                .build();

        // RequestBody.fromInputStream требует contentLength >= 0,
        // если неизвестно — можно читать в temp файл или использовать SDK buffer
        // (не рекомендую для больших файлов).
        long len = contentLength >= 0 ? contentLength : 0L;
        s3.putObject(por, RequestBody.fromInputStream(in, len));

        return key;
    }

    /**
     * Сгенерировать presigned GET URL (в секундах)
     */
    public URL presignGetUrl(String key, long seconds) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(s3Configuration.bucket())
                .key(key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getReq)
                .signatureDuration(Duration.ofSeconds(seconds))
                .build();
        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
        return presigned.url();
    }

    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3Configuration.bucket())
                .key(key)
                .build()
        );
    }

    private String sanitize(String name) {
        if (name == null) return UUID.randomUUID().toString();
        return name.replaceAll("[^a-zA-Zа-яА-Я0-9.\\-_]", "_");
    }
}
