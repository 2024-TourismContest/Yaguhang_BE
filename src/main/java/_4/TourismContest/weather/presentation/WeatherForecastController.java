package _4.TourismContest.weather.presentation;

import _4.TourismContest.weather.application.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class WeatherForecastController {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @GetMapping("/fetch-forecast")
    public void fetchForecast(@RequestParam String baseDate, @RequestParam String baseTime,
                              @RequestParam int nx, @RequestParam int ny) throws IOException {
        weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
    }
}
