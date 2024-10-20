package com.weather.weather.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter

public class AlertDTO {
    private String city;
    private boolean alertTriggered;
    private String alertMessage;
}
