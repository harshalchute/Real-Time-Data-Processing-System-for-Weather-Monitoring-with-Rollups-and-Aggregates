package com.weather.weather.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DailyWeatherSummaryDTO {
    private String city;
    private double avgTemp;
    private double maxTemp;
    private double minTemp;
    private String dominantCondition;
}
