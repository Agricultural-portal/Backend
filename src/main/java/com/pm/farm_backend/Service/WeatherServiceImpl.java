package com.pm.farm_backend.Service;

import com.pm.farm_backend.Dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public WeatherServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CurrentWeatherResponseDto getCurrentWeather(String city, String country) {
        try {
            String location = country != null && !country.isEmpty() 
                ? city + "," + country 
                : city;

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
                    .queryParam("q", location)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            log.info("Fetching weather for location: {} (URL: {})", location, url.replace(apiKey, "***"));
            WeatherDto weatherDto = restTemplate.getForObject(url, WeatherDto.class);
            return mapToCurrentWeatherResponse(weatherDto);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching weather for {}: {} - {}", city, e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("City '" + city + "' not found. Please check the city name and try again.");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Invalid API key. Please contact administrator.");
            } else {
                throw new RuntimeException("Weather service error: " + e.getMessage());
            }
        } catch (RestClientException e) {
            log.error("Error fetching weather data for city: {}, country: {}", city, country, e);
            throw new RuntimeException("Failed to connect to weather service. Please try again later.");
        }
    }

    @Override
    public CurrentWeatherResponseDto getCurrentWeatherByCoordinates(Double latitude, Double longitude) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/weather")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            log.info("Fetching weather for coordinates: lat={}, lon={}", latitude, longitude);
            WeatherDto weatherDto = restTemplate.getForObject(url, WeatherDto.class);
            return mapToCurrentWeatherResponse(weatherDto);
        } catch (RestClientException e) {
            log.error("Error fetching weather data for coordinates: lat={}, lon={}", latitude, longitude, e);
            throw new RuntimeException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    @Override
    public ForecastResponseDto getWeatherForecast(String city, String country) {
        try {
            String location = country != null && !country.isEmpty() 
                ? city + "," + country 
                : city;

            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/forecast")
                    .queryParam("q", location)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            log.info("Fetching forecast for location: {} (URL: {})", location, url.replace(apiKey, "***"));
            WeatherForecastDto forecastDto = restTemplate.getForObject(url, WeatherForecastDto.class);
            return mapToForecastResponse(forecastDto);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching forecast for {}: {} - {}", city, e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("City '" + city + "' not found. Please check the city name and try again.");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Invalid API key. Please contact administrator.");
            } else {
                throw new RuntimeException("Weather service error: " + e.getMessage());
            }
        } catch (RestClientException e) {
            log.error("Error fetching forecast data for city: {}, country: {}", city, country, e);
            throw new RuntimeException("Failed to connect to weather service. Please try again later.");
        }
    }

    @Override
    public ForecastResponseDto getWeatherForecastByCoordinates(Double latitude, Double longitude) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/forecast")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("appid", apiKey)
                    .queryParam("units", "metric")
                    .build()
                    .toUriString();

            log.info("Fetching forecast for coordinates: lat={}, lon={}", latitude, longitude);
            WeatherForecastDto forecastDto = restTemplate.getForObject(url, WeatherForecastDto.class);
            return mapToForecastResponse(forecastDto);
        } catch (RestClientException e) {
            log.error("Error fetching forecast data for coordinates: lat={}, lon={}", latitude, longitude, e);
            throw new RuntimeException("Failed to fetch forecast data: " + e.getMessage(), e);
        }
    }

    private CurrentWeatherResponseDto mapToCurrentWeatherResponse(WeatherDto weatherDto) {
        if (weatherDto == null) {
            return null;
        }

        CurrentWeatherResponseDto response = new CurrentWeatherResponseDto();
        response.setLocation(weatherDto.getName());
        response.setCountry(weatherDto.getSys() != null ? weatherDto.getSys().getCountry() : null);
        
        if (weatherDto.getMain() != null) {
            response.setTemperature(weatherDto.getMain().getTemp());
            response.setFeelsLike(weatherDto.getMain().getFeelsLike());
            response.setHumidity(weatherDto.getMain().getHumidity());
            response.setPressure(weatherDto.getMain().getPressure());
        }
        
        if (weatherDto.getWeather() != null && !weatherDto.getWeather().isEmpty()) {
            WeatherDto.Weather weather = weatherDto.getWeather().get(0);
            response.setDescription(weather.getDescription());
            response.setIcon(weather.getIcon());
        }
        
        if (weatherDto.getWind() != null) {
            response.setWindSpeed(weatherDto.getWind().getSpeed());
        }
        
        response.setVisibility(weatherDto.getVisibility());
        
        if (weatherDto.getSys() != null) {
            response.setSunrise(weatherDto.getSys().getSunrise());
            response.setSunset(weatherDto.getSys().getSunset());
        }
        
        return response;
    }

    private ForecastResponseDto mapToForecastResponse(WeatherForecastDto forecastDto) {
        if (forecastDto == null || forecastDto.getList() == null) {
            return null;
        }

        ForecastResponseDto response = new ForecastResponseDto();
        
        if (forecastDto.getCity() != null) {
            response.setLocation(forecastDto.getCity().getName());
            response.setCountry(forecastDto.getCity().getCountry());
        }

        // Group forecasts by date and take one per day (noon time preferred)
        Map<LocalDate, List<WeatherForecastDto.ForecastItem>> groupedByDate = forecastDto.getList()
                .stream()
                .collect(Collectors.groupingBy(item -> 
                    Instant.ofEpochSecond(item.getDt())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                ));

        List<ForecastResponseDto.DailyForecast> dailyForecasts = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        groupedByDate.forEach((date, items) -> {
            // Prefer the forecast around noon (12:00)
            WeatherForecastDto.ForecastItem selectedItem = items.stream()
                    .filter(item -> item.getDateText() != null && item.getDateText().contains("12:00:00"))
                    .findFirst()
                    .orElse(items.get(0));

            ForecastResponseDto.DailyForecast dailyForecast = new ForecastResponseDto.DailyForecast();
            dailyForecast.setDate(date.format(dateFormatter));
            dailyForecast.setTimestamp(selectedItem.getDt());
            
            if (selectedItem.getMain() != null) {
                dailyForecast.setTemperature(selectedItem.getMain().getTemp());
                dailyForecast.setTempMin(selectedItem.getMain().getTempMin());
                dailyForecast.setTempMax(selectedItem.getMain().getTempMax());
                dailyForecast.setHumidity(selectedItem.getMain().getHumidity());
            }
            
            if (selectedItem.getWeather() != null && !selectedItem.getWeather().isEmpty()) {
                WeatherForecastDto.Weather weather = selectedItem.getWeather().get(0);
                dailyForecast.setDescription(weather.getDescription());
                dailyForecast.setIcon(weather.getIcon());
            }
            
            if (selectedItem.getWind() != null) {
                dailyForecast.setWindSpeed(selectedItem.getWind().getSpeed());
            }
            
            dailyForecast.setPrecipitationProbability(selectedItem.getPop());
            
            dailyForecasts.add(dailyForecast);
        });

        // Sort by date and limit to 5 days
        dailyForecasts.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        response.setForecasts(dailyForecasts.stream().limit(5).collect(Collectors.toList()));

        return response;
    }
}
