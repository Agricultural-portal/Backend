package com.pm.farm_backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeatherResponseDto {
    private String location;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private String description;
    private String icon;
    private Integer humidity;
    private Double windSpeed;
    private Integer pressure;
    private Integer visibility;
    private Long sunrise;
    private Long sunset;
}
