package com.weather.weather;

import com.weather.weather.DTO.AlertDTO;
import com.weather.weather.DTO.DailyWeatherSummaryDTO;
import com.weather.weather.Models.WeatherData;
import com.weather.weather.Repository.WeatherDataRepository;
import com.weather.weather.Service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

	@InjectMocks
	private WeatherService weatherService;

	@Mock
	private WeatherDataRepository weatherDataRepository;

	private WeatherData weatherData;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		weatherData = new WeatherData();
		weatherData.setCity("Delhi");
		weatherData.setTemperature(35.5);
		weatherData.setFeelsLike(37.0);
		weatherData.setMainCondition("Clear");
		weatherData.setTimestamp(LocalDateTime.now());
	}


	@Test
	void testCalculateDailySummary_NoData() {
		// Mock the weather data repository
		when(weatherDataRepository.findAllByCityAndTimestampBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
				.thenReturn(Collections.emptyList());

		// Call the service method
		DailyWeatherSummaryDTO summary = weatherService.calculateDailySummary("Delhi");

		// Assert empty summary response
		assertNotNull(summary);
		assertEquals("Delhi", summary.getCity());
		assertEquals(0, summary.getAvgTemp());
		assertEquals(0, summary.getMaxTemp());
		assertEquals(0, summary.getMinTemp());
		assertEquals("No Data", summary.getDominantCondition());
	}


	@Test
	void testCheckAndAlert_TemperatureBelowThreshold() {
		// Mock the repository to return latest weather data
		WeatherData latestWeather = new WeatherData("Delhi", 30.0, 31.0, "Clear", LocalDateTime.now());
		when(weatherDataRepository.findTopByCityOrderByTimestampDesc(anyString())).thenReturn(latestWeather);

		// Call the service method
		AlertDTO alert = weatherService.checkAndAlert("Delhi");

		// Assert that no alert is triggered
		assertFalse(alert.isAlertTriggered());
		assertEquals("No alert triggered. Temperature is within safe limits.", alert.getAlertMessage());
	}
}
