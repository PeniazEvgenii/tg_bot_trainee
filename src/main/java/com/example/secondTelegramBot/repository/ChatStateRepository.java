package com.example.secondTelegramBot.repository;

import com.example.secondTelegramBot.repository.entity.ChatStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatStateRepository extends JpaRepository<ChatStateEntity, Long> {

    List<ChatStateEntity> findAllByCurrentStateNot(String currentState);
}
