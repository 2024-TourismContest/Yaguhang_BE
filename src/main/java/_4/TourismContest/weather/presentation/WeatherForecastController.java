package _4.TourismContest.weather.presentation;

import _4.TourismContest.weather.application.WeatherForecastService;
import _4.TourismContest.weather.domain.WeatherForecast;
import _4.TourismContest.weather.dto.WeatherForecastDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "날씨 업데이트 API, 사용하지 말것")
    public void fetchForecast(@RequestParam String baseDate, @RequestParam String baseTime,
                              @RequestParam int nx, @RequestParam int ny) throws IOException {
        weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
    }
//    @GetMapping("/weatherOfStadium")
//    @Operation(summary = "구장 날씨 조회", description = "구장 이름을 입력 시 현재 시간부터 size만큼의 날씨 데이터 출력(페이지네이션 적용)")
//    public Page<WeatherForecast> getNowWeatherPerStadium(@RequestParam String stadium, int page, @RequestParam(defaultValue = "10") int size){
//        return weatherForecastService.findWeatherForecastDataPerHour(stadium, page, size);
//    }

    @GetMapping("/weatherOfGame")
    @Operation(summary = "경기 날씨 조회",description = "경기 ID를 입력 시 경기 시작 시간 기준으로 1시간 기준 날씨 데이터 출력")
    public WeatherForecastDTO getWeatherForecastDataPerHourByGame(@RequestParam Long gameId, int page, @RequestParam(defaultValue = "24") int size){
        return weatherForecastService.findWeatherForecastDataPerHourByGame(gameId,page,size);
    }
}
