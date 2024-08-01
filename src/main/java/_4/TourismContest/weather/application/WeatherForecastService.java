package _4.TourismContest.weather.application;

import _4.TourismContest.weather.domain.WeatherForecast;
import _4.TourismContest.weather.dto.WeatherApiResponse;
import _4.TourismContest.weather.repository.WeatherForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeatherForecastService {
    private static final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    
    @Value("${API.weather.key}")
    private String SERVICE_KEY;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherForecastRepository weatherForecastRepository;

    @Transactional
    public void fetchAndSaveForecastData(String baseDate, String baseTime, int nx, int ny) throws IOException {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .queryParam("numOfRows", 1000)
                .queryParam("pageNo", 1)
                .queryParam("dataType", "JSON")
                .encode()
                .build()
                .toUri();

        WeatherApiResponse response = restTemplate.getForObject(uri, WeatherApiResponse.class);

        if (response != null && response.getResponse() != null &&
                response.getResponse().getBody() != null &&
                response.getResponse().getBody().getItems() != null) {

            List<WeatherForecast> forecasts = response.getResponse().getBody().getItems().getItem().stream()
                    .map(item -> WeatherForecast.builder()
                            .baseDate(item.getBaseDate())
                            .baseTime(item.getBaseTime())
                            .category(item.getCategory())
                            .fcstDate(item.getFcstDate())
                            .fcstTime(item.getFcstTime())
                            .fcstValue(item.getFcstValue())
                            .nx(item.getNx())
                            .ny(item.getNy())
                            .build())
                    .collect(Collectors.toList());

            // 데이터베이스에서 기존 데이터 조회
            for (WeatherForecast forecast : forecasts) {
                List<WeatherForecast> existingForecasts = weatherForecastRepository.findByNxAndNyAndFcstDateAndFcstTimeAndCategory(
                        forecast.getNx(), forecast.getNy(), forecast.getFcstDate(), forecast.getFcstTime(), forecast.getCategory());

                if (!existingForecasts.isEmpty()) {
                    // 기존 데이터가 있는 경우, 비교 후 변경된 부분만 업데이트
                    for (WeatherForecast existing : existingForecasts) {
                        if (!existing.getFcstValue().equals(forecast.getFcstValue())) {
                            // 값이 변경된 경우 업데이트
                            existing.setFcstValue(forecast.getFcstValue());
                            weatherForecastRepository.save(existing);
                        }
                    }
                } else {
                    // 기존 데이터가 없는 경우 새로 저장
                    weatherForecastRepository.save(forecast);
                }
            }
        } else {
            throw new IOException("No data found in response.");
        }
    }
}