package com.example.secondTelegramBot.state;

import lombok.Builder;
/** Состояние пользователя + любая временная полезная нагрузка. */
@Builder
public record UserState(BotState state, String temp) {

    public static UserState idle() {
        return new UserState(BotState.IDLE, null);
    }

    public UserState withState(BotState newState) {
        return new UserState(newState, this.temp);
    }

    public UserState withTemp(String newTemp) {
        return new UserState(this.state, newTemp);
    }
}
