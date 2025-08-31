package com.example.secondTelegramBot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.stringtemplate.v4.ST;

@ConfigurationProperties(prefix = "app.s3")
public record S3Configuration(
        String bucket,
        String endpoint,
        String region,
        boolean pathStyleAccess,
        String awsAccessKeyId,
        String awsSecretAccessKey) {

}
