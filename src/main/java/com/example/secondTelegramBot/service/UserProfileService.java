package com.example.secondTelegramBot.service;

import com.example.secondTelegramBot.repository.UserProfileRepository;
import com.example.secondTelegramBot.repository.entity.UserProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileEntity saveProfile(UserProfileEntity userProfileEntity) {

        return userProfileRepository.save(userProfileEntity);
    }

    public Optional<UserProfileEntity> getProfile(Long chatId) {
        return userProfileRepository.findById(chatId);
    }
}
