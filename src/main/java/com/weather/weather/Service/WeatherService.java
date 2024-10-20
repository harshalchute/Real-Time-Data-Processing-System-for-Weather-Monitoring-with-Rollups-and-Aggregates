package com.weather.weather.Service;


import com.weather.weather.DTO.AlertDTO;
import com.weather.weather.DTO.DailyWeatherSummaryDTO;
import com.weather.weather.Models.WeatherData;
import com.weather.weather.Repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherDataRepository weatherDataRepository;

    @Autowired
    private JavaMailSender emailSender;

    private int consecutiveAlerts = 0;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final List<String> cities = Arrays.asList("Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad");



    public List<WeatherData> fetchWeatherDataForAllCities() {
        RestTemplate restTemplate = new RestTemplate();
        List<WeatherData> weatherDataList = new ArrayList<>();

        for (String city : cities) {
            String url = apiUrl.replace("{CITY}", city).replace("{API_KEY}", apiKey);
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                WeatherData weatherData = parseAndSaveWeatherData(response, city);
                weatherDataList.add(weatherData);
            }
        }
        return weatherDataList;
    }

    // Parse weather data and save it to the database
    private WeatherData parseAndSaveWeatherData(String response, String city){
        JSONObject json = new JSONObject(response);

        double tempKelvin = json.getJSONObject("main").getDouble("temp");
        double feelsLikeKelvin = json.getJSONObject("main").getDouble("feels_like");
        String mainCondition = json.getJSONArray("weather").getJSONObject(0).getString("main");

        WeatherData weatherData = new WeatherData();
        weatherData.setCity(city);
        weatherData.setTemperature(kelvinToCelsius(tempKelvin));
        weatherData.setFeelsLike(kelvinToCelsius(feelsLikeKelvin));
        weatherData.setMainCondition(mainCondition);
        weatherData.setTimestamp(LocalDateTime.now());

        // Save the data to the database
        weatherDataRepository.save(weatherData);

        // Return the WeatherData object
        return weatherData;
    }

    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }


    public DailyWeatherSummaryDTO calculateDailySummary(String city) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        List<WeatherData> data = weatherDataRepository.findAllByCityAndTimestampBetween(city, start, end);

        if (!data.isEmpty()) {
            // Calculate average temperature
            OptionalDouble avgTemp = data.stream().mapToDouble(WeatherData::getTemperature).average();

            // Calculate maximum and minimum temperatures
            double maxTemp = data.stream().mapToDouble(WeatherData::getTemperature).max().orElse(0);
            double minTemp = data.stream().mapToDouble(WeatherData::getTemperature).min().orElse(0);

            // Calculate the dominant weather condition, filtering out null values
            String dominantCondition = data.stream()
                    .map(WeatherData::getMainCondition)
                    .filter(Objects::nonNull) // Filter out null conditions
                    .collect(Collectors.groupingBy(condition -> condition, Collectors.counting()))
                    .entrySet().stream()
                    .max(Comparator.comparingLong(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse("No Dominant Condition");

            // Return the summary as an object
            return new DailyWeatherSummaryDTO(city, avgTemp.orElse(0), maxTemp, minTemp, dominantCondition);
        }

        // Return empty values if no data is available
        return new DailyWeatherSummaryDTO(city, 0, 0, 0, "No Data");
    }


    @Scheduled(fixedRateString = "${weather.check.interval}")
    public void checkAlertsForAllCities() {
        // Iterate through the list of cities
        for (String city : List.of("Delhi", "Mumbai", "Chennai", "Bangalore", "Kolkata", "Hyderabad")) {
            AlertDTO alert = checkAndAlert(city);
            if (alert.isAlertTriggered()) {
                consecutiveAlerts++;
                if (consecutiveAlerts >= 2) {
                    sendEmailNotification(alert);
                    consecutiveAlerts = 0; // Reset the counter after sending the alert
                }
            } else {
                consecutiveAlerts = 0; // Reset the counter if no alert is triggered
            }
        }
    }
    public AlertDTO checkAndAlert(String city) {
        WeatherData latest = weatherDataRepository.findTopByCityOrderByTimestampDesc(city);
        if (latest != null && latest.getTemperature() > 34.0) {
            String message = "ALERT: Temperature exceeds 34°C in " + city + "!";
            System.out.println(message);
            return new AlertDTO(city, true, message);
        }
        return new AlertDTO(city, false, "No alert triggered. Temperature is within safe limits.");
    }

    private void sendEmailNotification(AlertDTO alert) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vnighvekar0127@gmail.com"); // Replace with actual recipient email
        message.setSubject("Weather Alert for " + alert.getCity());
        message.setText(alert.getAlertMessage());

        try {
            emailSender.send(message);
            System.out.println("Email sent: " + alert.getAlertMessage());
        } catch (MailException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace(); // Log the exception for debugging
        }
    }
}