package com.example.secondTelegramBot.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weather", url = "${api.weather.url}")
public interface WeatherClient {

    @GetMapping
    public Weather getWeather(@RequestParam("q") String location,
                           @RequestParam("lang") String lang,
                           @RequestParam("key") String token);
}
