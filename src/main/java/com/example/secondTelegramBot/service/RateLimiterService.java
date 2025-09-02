package com.example.secondTelegramBot.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.OptionalLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    private final ConcurrentMap<Long, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket newBucket() {
        Refill refill = Refill.intervally(1, Duration.ofSeconds(10)); // 5 tokens per sec
        Bandwidth limit = Bandwidth.classic(3, refill); // burst up to 10
        return Bucket4j.builder().addLimit(limit).build();
    }

    public boolean tryConsume(Long chatId) {
        Bucket bucket = buckets.computeIfAbsent(chatId, id -> newBucket());
        return bucket.tryConsume(1);
    }

    // или с фидбеком: сколько ждать в секундах
    public OptionalLong tryConsumeWithWaitSeconds(Long chatId) {
        Bucket bucket = buckets.computeIfAbsent(chatId, id -> newBucket());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            return OptionalLong.of(0L);
        } else {
            long nanosToWait = probe.getNanosToWaitForRefill();
            long seconds = TimeUnit.NANOSECONDS.toSeconds(nanosToWait) + 1; // округлять вверх
            return OptionalLong.of(seconds);
        }
    }
}
