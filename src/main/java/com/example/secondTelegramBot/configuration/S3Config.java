package com.example.secondTelegramBot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Configuration s3Configuration;

    @Bean
    public S3Client s3Client() {

        var builder = S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(Region.of(s3Configuration.region()));

        String key = s3Configuration.awsAccessKeyId();
        String secret = s3Configuration.awsSecretAccessKey();
        if (key != null && secret != null) {
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret)));
        }

        if (s3Configuration.endpoint() != null && !s3Configuration.endpoint().isBlank()) {
            builder.endpointOverride(URI.create(s3Configuration.endpoint()));
            if (s3Configuration.pathStyleAccess()) {
                builder.serviceConfiguration(cfg -> cfg.pathStyleAccessEnabled(true));
            }
        }
        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner() {

        var builder = S3Presigner.builder().region(Region.of(s3Configuration.region()));
        String key = s3Configuration.awsAccessKeyId();
        String secret = s3Configuration.awsSecretAccessKey();
        if (key != null && secret != null) {
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret)));
        }
        if (s3Configuration.endpoint() != null && !s3Configuration.endpoint().isBlank()) {
            builder.endpointOverride(URI.create(s3Configuration.endpoint()));
        }
        return builder.build();
    }
}
