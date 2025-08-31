package com.example.secondTelegramBot.repository;

import com.example.secondTelegramBot.repository.entity.MediaFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaFileRepository extends JpaRepository<MediaFileEntity, Long> {

    List<MediaFileEntity> findAllByChatId(Long chatId);
}
