package com.example.secondTelegramBot.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final WeatherClient weatherClient;
    private final String token;
    private final String lang;
    private final String location;

    public WeatherService(WeatherClient weatherClient,
                          @Value("${api.weather.token}") String token,
                          @Value("${api.weather.lang}") String lang,
                          @Value("${api.weather.q}") String location) {
        this.weatherClient = weatherClient;
        this.token = token;
        this.lang = lang;
        this.location = location;
    }

    public Weather getWeather() {
        return weatherClient.getWeather(location, lang, token);
    }
}
