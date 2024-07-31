package _4.TourismContest.weather.scheduler;

import _4.TourismContest.weather.application.WeatherForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WeatherForecastScheduler {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void fetchForecastData() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime previousHour = now.minusHours(1);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");

        String baseDate = previousHour.format(dateFormatter);
        String baseTime = previousHour.format(timeFormatter);

        System.out.println("baseTime = " + baseTime);
        int nx = 55; // 경기장 X 좌표
        int ny = 127; // 경기장 Y 좌표

        weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
    }
}