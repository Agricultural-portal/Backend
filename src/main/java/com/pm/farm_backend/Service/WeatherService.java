package com.pm.farm_backend.Service;

import com.pm.farm_backend.Dto.CurrentWeatherResponseDto;
import com.pm.farm_backend.Dto.ForecastResponseDto;

public interface WeatherService {
    
    /**
     * Get current weather by city and optional country
     * @param city City name
     * @param country Country code (optional, e.g., "in" for India)
     * @return Current weather information
     */
    CurrentWeatherResponseDto getCurrentWeather(String city, String country);
    
    /**
     * Get current weather by coordinates
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Current weather information
     */
    CurrentWeatherResponseDto getCurrentWeatherByCoordinates(Double latitude, Double longitude);
    
    /**
     * Get 5-day weather forecast by city and optional country
     * @param city City name
     * @param country Country code (optional, e.g., "in" for India)
     * @return Weather forecast for next 5 days
     */
    ForecastResponseDto getWeatherForecast(String city, String country);
    
    /**
     * Get 5-day weather forecast by coordinates
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Weather forecast for next 5 days
     */
    ForecastResponseDto getWeatherForecastByCoordinates(Double latitude, Double longitude);
}
