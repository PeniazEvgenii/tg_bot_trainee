package com.example.secondTelegramBot.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private Location location;
    private Current current;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @Getter
    public static class Location {
        private String name;
        private String localtime;
        @JsonProperty("tz_id")
        private String timeZone;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @Getter
    public static class Current {
        @JsonProperty("temp_c")
        private double temperature;
        private double humidity;
    }
}
