package _4.TourismContest.weather.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.weather.domain.WeatherForecast;
import _4.TourismContest.weather.dto.WeatherApiResponse;
import _4.TourismContest.weather.repository.WeatherForecastRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {
    private static final String API_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    
    @Value("${API.weather.key}")
    private String SERVICE_KEY;

    @Autowired
    private RestTemplate restTemplate;

    private final WeatherForecastRepository weatherForecastRepository;
    private final StadiumRepository stadiumRepository;
    /**
     * 조회하는 시간 기준으로, 1시간 단위의 날씨 데이터 조회
     * @param stadium
     * @return
     */
    public Page<WeatherForecast> findWeatherForecastDataPerHour(String stadium, int page, int size){
        Optional<Stadium> optionalStadium = stadiumRepository.findByName(stadium);
        if(optionalStadium.isEmpty()){
            throw new IllegalArgumentException("Illegal Stadium Name");
        }else{
            Stadium stadium1 = optionalStadium.get();
            LocalDateTime now = LocalDateTime.now();
            Page<WeatherForecast> allByNxAndNyAndCategoryAndFcstTimeIsAfter = weatherForecastRepository.findByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "SKY", now, PageRequest.of(page,size));

            return allByNxAndNyAndCategoryAndFcstTimeIsAfter;
        }
    }

    /**
     * 야구 경기 일정에 들어가는 날씨값 반환
     * @param game
     * @return
     */
    public String getWeatherForecastDataWithGame(Baseball game){
        String stadium = game.getLocation();
        LocalDateTime gameTime = game.getTime();
        Optional<Stadium> optionalStadium = stadiumRepository.findByName(stadium);
        if(optionalStadium.isEmpty()){
            throw new IllegalArgumentException("Illegal Stadium Name");
        }else{
            Stadium stadium1 = optionalStadium.get();
            Optional<WeatherForecast> pcpOptional = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "PCP", gameTime);   //1시간 강수량
            Optional<WeatherForecast> popOptional = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "POP", gameTime);   //강수확률
            Optional<WeatherForecast> ptyOptional = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "PTY", gameTime);   //강수형태
            Optional<WeatherForecast> skyOptional = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "SKY", gameTime);   //하늘형태
            Optional<WeatherForecast> tmpOptional = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "TMP", gameTime);   //1시간 기온
            if(pcpOptional.isEmpty() || popOptional.isEmpty() || ptyOptional.isEmpty() || skyOptional.isEmpty() || tmpOptional.isEmpty()){
                throw new IllegalStateException("No data found in weatherRepositoy");
            }else{
                WeatherForecast pcp = pcpOptional.get();
                //비가 안오는 경우
                if(pcp.getFcstValue().equals("강수없음")){
                    return null;
                }else{
                    //비가 오는 경우
                    return null;
                }
//                return pcp.get().toString();
            }
//            Optional<WeatherForecast> PCP = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "PCP" ,gameTime);
//            Optional<WeatherForecast> SKY = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(stadium1.getNx(), stadium1.getNy(), "SKY" ,gameTime);
//            if(PTY.isEmpty()){
//                throw new IllegalStateException("No data found");
//            }else{
//                WeatherForecast weatherForecast = PCP.get();
//                if(!weatherForecast.getFcstValue().equals("강수없음")){
//                    //비가 오는 경우
//
//                }
//                return weatherForecast.toString();
//            }
        }
    }
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
                    .map(item -> {
                        LocalDateTime fcstDateTime = LocalDateTime.parse(item.getFcstDate() + item.getFcstTime(),
                                DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                        return WeatherForecast.builder()
                                .baseDate(item.getBaseDate())
                                .baseTime(item.getBaseTime())
                                .category(item.getCategory())
                                .fcstTime(fcstDateTime)
                                .fcstValue(item.getFcstValue())
                                .nx(item.getNx())
                                .ny(item.getNy())
                                .build();
                    })
                    .collect(Collectors.toList());


            // 데이터베이스에서 기존 데이터 조회
            for (WeatherForecast forecast : forecasts) {
                List<WeatherForecast> existingForecasts = weatherForecastRepository.findByNxAndNyAndFcstTimeAndCategory(
                        forecast.getNx(), forecast.getNy(), forecast.getFcstTime(), forecast.getCategory());

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