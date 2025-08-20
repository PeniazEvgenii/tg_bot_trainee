package com.example.secondTelegramBot.repository;

import com.example.secondTelegramBot.repository.entity.MessageLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLogEntity, Long> {
}
