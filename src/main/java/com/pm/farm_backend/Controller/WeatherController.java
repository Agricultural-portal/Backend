package com.pm.farm_backend.Controller;

import com.pm.farm_backend.Dto.CurrentWeatherResponseDto;
import com.pm.farm_backend.Dto.ForecastResponseDto;
import com.pm.farm_backend.Service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather forecast and current weather information")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    @Operation(summary = "Get current weather by city", 
               description = "Fetch current weather information for a specific city and optional country")
    public ResponseEntity<CurrentWeatherResponseDto> getCurrentWeather(
            @Parameter(description = "City name", example = "Mumbai")
            @RequestParam String city,
            
            @Parameter(description = "Country code (optional)", example = "in")
            @RequestParam(required = false) String country) {
        
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter is required and cannot be empty");
        }
        
        CurrentWeatherResponseDto weather = weatherService.getCurrentWeather(city.trim(), country);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/current/coordinates")
    @Operation(summary = "Get current weather by coordinates", 
               description = "Fetch current weather information using latitude and longitude")
    public ResponseEntity<CurrentWeatherResponseDto> getCurrentWeatherByCoordinates(
            @Parameter(description = "Latitude", example = "19.0760")
            @RequestParam Double lat,
            
            @Parameter(description = "Longitude", example = "72.8777")
            @RequestParam Double lon) {
        
        CurrentWeatherResponseDto weather = weatherService.getCurrentWeatherByCoordinates(lat, lon);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get 5-day weather forecast by city", 
               description = "Fetch 5-day weather forecast for a specific city and optional country")
    public ResponseEntity<ForecastResponseDto> getWeatherForecast(
            @Parameter(description = "City name", example = "Mumbai")
            @RequestParam String city,
            
            @Parameter(description = "Country code (optional)", example = "in")
            @RequestParam(required = false) String country) {
        
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter is required and cannot be empty");
        }
        
        ForecastResponseDto forecast = weatherService.getWeatherForecast(city.trim(), country);
        return ResponseEntity.ok(forecast);
    }

    @GetMapping("/forecast/coordinates")
    @Operation(summary = "Get 5-day weather forecast by coordinates", 
               description = "Fetch 5-day weather forecast using latitude and longitude")
    public ResponseEntity<ForecastResponseDto> getWeatherForecastByCoordinates(
            @Parameter(description = "Latitude", example = "19.0760")
            @RequestParam Double lat,
            
            @Parameter(description = "Longitude", example = "72.8777")
            @RequestParam Double lon) {
        
        ForecastResponseDto forecast = weatherService.getWeatherForecastByCoordinates(lat, lon);
        return ResponseEntity.ok(forecast);
    }
}
