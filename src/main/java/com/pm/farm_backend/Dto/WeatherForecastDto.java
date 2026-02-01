package com.pm.farm_backend.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecastDto {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {
        private Long id;
        private String name;
        private Coordinates coord;
        private String country;
        private Integer population;
        private Integer timezone;
        private Long sunrise;
        private Long sunset;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinates {
        @JsonProperty("lat")
        private Double latitude;
        
        @JsonProperty("lon")
        private Double longitude;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ForecastItem {
        private Long dt;
        private MainWeather main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        private Integer visibility;
        private Double pop; // Probability of precipitation
        private Rain rain;
        private Sys sys;
        
        @JsonProperty("dt_txt")
        private String dateText;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainWeather {
        private Double temp;
        
        @JsonProperty("feels_like")
        private Double feelsLike;
        
        @JsonProperty("temp_min")
        private Double tempMin;
        
        @JsonProperty("temp_max")
        private Double tempMax;
        
        private Integer pressure;
        
        @JsonProperty("sea_level")
        private Integer seaLevel;
        
        @JsonProperty("grnd_level")
        private Integer groundLevel;
        
        private Integer humidity;
        
        @JsonProperty("temp_kf")
        private Double tempKf;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        private Integer all;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private Double speed;
        private Integer deg;
        private Double gust;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rain {
        @JsonProperty("3h")
        private Double threeHours;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private String pod; // Part of day (d = day, n = night)
    }

    // Main Forecast Response Fields
    private String cod;
    private Integer message;
    private Integer cnt;
    private List<ForecastItem> list;
    private City city;
}
