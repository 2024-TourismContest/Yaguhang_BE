package _4.TourismContest.weather.presentation;

import _4.TourismContest.weather.application.WeatherForecastService;
import _4.TourismContest.weather.domain.WeatherForecast;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class WeatherForecastController {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @GetMapping("/fetch-forecast")
    public void fetchForecast(@RequestParam String baseDate, @RequestParam String baseTime,
                              @RequestParam int nx, @RequestParam int ny) throws IOException {
        weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
    }
    @GetMapping("/nowWeatherPerStadium")
    public Page<WeatherForecast> getNowWeatherPerStadium(@RequestParam String stadium, int page, @RequestParam(defaultValue = "10") int size){
        return weatherForecastService.findWeatherForecastDataPerHour(stadium, page, size);
    }
}
