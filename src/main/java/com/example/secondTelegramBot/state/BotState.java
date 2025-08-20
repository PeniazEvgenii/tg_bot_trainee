package com.example.secondTelegramBot.state;

public enum BotState {
    IDLE,              // нет активного сценария
    WAITING_FOR_NAME,  // анкета: имя
    WAITING_FOR_AGE,   // анкета: возраст
    WAITING_FOR_CITY,   // анкета: город
    ECHO_MODE,
    AI_MODE
}
