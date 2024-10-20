package com.weather.weather.Repository;


import com.weather.weather.Models.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    List<WeatherData> findAllByCityAndTimestampBetween(String city, LocalDateTime start, LocalDateTime end);
    WeatherData findTopByCityOrderByTimestampDesc(String city);
}
