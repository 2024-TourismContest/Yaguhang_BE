package _4.TourismContest.weather.scheduler;

import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.weather.application.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WeatherForecastScheduler {

    private final StadiumRepository stadiumRepository;
    private final WeatherForecastService weatherForecastService;

    @Scheduled(cron = "10 10 2,5,8,11,14,17,20,23 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void fetchForecastData() throws IOException {

        List<Stadium> stadiumList = stadiumRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");

        String baseDate = now.format(dateFormatter);
        String baseTime = now.format(timeFormatter);
//        baseTime = "1400";
        System.out.println("baseTime = " + baseTime);

        for (Stadium stadium : stadiumList) {
            int nx = stadium.getNx(); // 경기장 X 좌표
            int ny = stadium.getNy(); // 경기장 Y 좌표
            weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
        }
    }
}