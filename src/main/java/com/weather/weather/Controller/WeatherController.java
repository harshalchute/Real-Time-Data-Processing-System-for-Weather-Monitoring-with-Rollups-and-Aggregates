package com.weather.weather.Controller;


import com.weather.weather.DTO.AlertDTO;
import com.weather.weather.DTO.DailyWeatherSummaryDTO;
import com.weather.weather.Models.WeatherData;
import com.weather.weather.Service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    // Fetch weather data for all cities and return as JSON
    @GetMapping("/fetch")
    public ResponseEntity<List<WeatherData>> fetchWeatherDataForAllCities() {
        // Trigger the weather data fetching process
        List<WeatherData> weatherDataList = weatherService.fetchWeatherDataForAllCities();

        // Return the weather data list as JSON response
        return ResponseEntity.ok(weatherDataList);
    }

    @GetMapping("/summary/{city}")
    public ResponseEntity<DailyWeatherSummaryDTO> calculateDailySummary(@PathVariable String city) {
        // Fetch the daily summary from the service
        DailyWeatherSummaryDTO summary = weatherService.calculateDailySummary(city);

        // Return the summary as JSON
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/alert/{city}")
    public ResponseEntity<AlertDTO> checkWeatherAlert(@PathVariable String city) {
        // Fetch the alert status from the service
        AlertDTO alert = weatherService.checkAndAlert(city);

        // Return the alert as JSON
        return ResponseEntity.ok(alert);
    }
}