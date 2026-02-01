package com.pm.farm_backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForecastResponseDto {
    private String location;
    private String country;
    private List<DailyForecast> forecasts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyForecast {
        private String date;
        private Long timestamp;
        private Double temperature;
        private Double tempMin;
        private Double tempMax;
        private String description;
        private String icon;
        private Integer humidity;
        private Double windSpeed;
        private Double precipitationProbability;
    }
}
