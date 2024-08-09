package _4.TourismContest.weather.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.weather.domain.WeatherForecast;
import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import _4.TourismContest.weather.dto.WeatherApiResponse;
import _4.TourismContest.weather.dto.WeatherForecastDTO;
import _4.TourismContest.weather.dto.WeatherForecastPerDayDTO;
import _4.TourismContest.weather.dto.WeatherForecastPerHourDTO;
import _4.TourismContest.weather.repository.WeatherForecastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final BaseballRepository baseballRepository;

    /**
     * 조회하는 시간 -1 기준으로, 1시간 단위의 날씨 데이터 조회
     * @param stadium
     * @return
     */
    public Page<WeatherForecast> findWeatherForecastDataPerHour(String stadium, int page, int size) {
        Stadium stadiumEntity = stadiumRepository.findByName(stadium)
                .orElseThrow(() -> new IllegalArgumentException("Illegal Stadium Name"));

        LocalDateTime now = LocalDateTime.now().minusHours(1L);

        return weatherForecastRepository.findByNxAndNyAndCategoryAndFcstTimeIsAfter(
                stadiumEntity.getNx(),
                stadiumEntity.getNy(),
                "SKY",
                now,
                PageRequest.of(page, size)
        );
    }

    /**
     * 경기를 알고 있을 경우, 각 경기 시작시간 기준 1시간 단위의 날씨 데이터 조회(24시간까지), 페이지네이션 적용
     * @param stadium
     * @return
     */
    public WeatherForecastDTO findWeatherForecastDataPerHourByGame(Long baseBallId, int page, int size) {
        Baseball game = baseballRepository.findById(baseBallId)
                .orElseThrow(() -> new IllegalArgumentException("Illegal Baseball ID"));

        LocalDateTime gameTime = game.getTime().minusHours(1L);

        Stadium stadium = stadiumRepository.findByName(game.getLocation())
                .orElseThrow(() -> new IllegalArgumentException("Illegal Stadium Name"));

        Pageable pageable = PageRequest.of(page, size);
        Page<WeatherForecast> weatherForecastPage = weatherForecastRepository.findByNxAndNyAndCategoryAndFcstTimeIsAfter(
                stadium.getNx(),
                stadium.getNy(),
                "SKY",
                gameTime,
                pageable
        );

        if (weatherForecastPage.isEmpty()) {
            // No data found
            return null;
        }

        List<WeatherForecastPerHourDTO> weatherForecastDTOs = weatherForecastPage.stream().map(sky -> {
            String ptyValue = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                            stadium.getNx(), stadium.getNy(), "PTY", sky.getFcstTime().minusHours(1L))
                    .map(WeatherForecast::getFcstValue)
                    .orElse("0");

            WeatherForecastEnum weatherForecastEnum;
            if ("1".equals(sky.getFcstValue())) {
                weatherForecastEnum = getWeatherForecastForClearSky(ptyValue);
            } else if ("3".equals(sky.getFcstValue())) {
                weatherForecastEnum = getWeatherForecastForCloudySky(ptyValue);
            } else if ("4".equals(sky.getFcstValue())) {
                weatherForecastEnum = getWeatherForecastForOvercastSky(ptyValue);
            } else {
                throw new IllegalStateException("Unexpected sky value");
            }

            int popValue = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                            stadium.getNx(), stadium.getNy(), "POP", sky.getFcstTime().minusHours(1L))
                    .map(f -> Integer.parseInt(f.getFcstValue()))
                    .orElse(0);

            int tmpValue = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                            stadium.getNx(), stadium.getNy(), "TMP", sky.getFcstTime().minusHours(1L))
                    .map(f -> Integer.parseInt(f.getFcstValue()))
                    .orElse(0);

            return WeatherForecastPerHourDTO.builder()
                    .fcstDate(sky.getFcstTime().toLocalDate().toString())
                    .fcstTime(String.format("%02d", sky.getFcstTime().getHour()))
                    .weatherForecast(weatherForecastEnum)
                    .weatherImgUrl(getColoredWeatherUrl(weatherForecastEnum))
                    .rainyPercent(popValue)
                    .temp(tmpValue)
                    .build();
        }).collect(Collectors.toList());

        return WeatherForecastDTO.builder()
                .pageIndex(weatherForecastPage.getNumber())
                .size(weatherForecastPage.getSize())
                .date(game.getTime().toLocalDate().toString())
                .weathers(weatherForecastDTOs)
                .build();
    }

    /**
     * 날씨에 따라 날씨 로고 URL을 반환하는 함수
     * @param weatherForecastDataWithGame
     * @return
     */
    private String getColoredWeatherUrl(WeatherForecastEnum weatherForecastDataWithGame) {
        if(weatherForecastDataWithGame == null){
            return null;
        }
        String baseUrl = "https://yaguhang.kro.kr:8443/coloredWeatherImages/";
        switch (weatherForecastDataWithGame){
            case CLOUDY -> {
                return baseUrl + "Cloudy.svg";
            }
            case OVERCAST -> {
                return baseUrl + "Overcast.svg";
            }
            case RAINY -> {
                return baseUrl + "Rain.svg";
            }
            case SHOWER -> {
                return baseUrl + "Shower.svg";
            }
            case SNOW -> {
                return baseUrl + "Snow.svg";
            }
            case SUNNY -> {
                return baseUrl + "Sunny.svg";
            }
            default -> {
                throw new IllegalArgumentException("Check Weather Status");
            }
        }
    }

    /**
     * 특정 경기장의 하루 날씨 조회
     */
    public WeatherForecastPerDayDTO findWeatherForecastDataPerDay(Long baseBallId) {
        Baseball game = getBaseballGameById(baseBallId);
        Stadium stadium = getStadiumByName(game.getLocation());

        LocalDateTime gameTime = game.getTime().minusHours(1L);
        LocalDateTime startOfDay = gameTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = gameTime.toLocalDate().atTime(LocalTime.MAX);

        double minTemp = getWeatherDataForCategory(stadium, "TMN", gameTime);
        double maxTemp = getWeatherDataForCategory(stadium, "TMX", gameTime);
        double humidity = getWeatherDataForCategory(stadium, "REH", gameTime);
        double temp = getWeatherDataForCategory(stadium, "TMP", gameTime);
        double totalRainfall = calculateTotalRainfall(stadium, startOfDay, endOfDay);

        return buildWeatherForecastPerDayDTO(minTemp, maxTemp, humidity, totalRainfall, temp, game.getLocation(),game);
    }

    private Baseball getBaseballGameById(Long baseBallId) {
        return baseballRepository.findById(baseBallId)
                .orElseThrow(() -> new IllegalArgumentException("Illegal Baseball ID"));
    }

    private Stadium getStadiumByName(String location) {
        return stadiumRepository.findByName(location)
                .orElseThrow(() -> new IllegalArgumentException("Illegal Stadium Name"));
    }

    private double getWeatherDataForCategory(Stadium stadium, String category, LocalDateTime gameTime) {
        return weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                        stadium.getNx(), stadium.getNy(), category, gameTime)
                .map(weather -> Double.parseDouble(weather.getFcstValue()))
                .orElseThrow(() -> new IllegalStateException("No data found in WeatherRepository"));
    }

    private double calculateTotalRainfall(Stadium stadium, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        List<WeatherForecast> rainyList = weatherForecastRepository.findAllByNxAndNyAndCategoryAndFcstTimeBetween(
                stadium.getNx(), stadium.getNy(), "PCP", startOfDay, endOfDay);

        double totalRainfall = 0;
        for (WeatherForecast forecast : rainyList) {
            totalRainfall += parseRainfall(forecast.getFcstValue());
        }
        return totalRainfall;
    }

    private double parseRainfall(String fcstValue) {
        if ("강수없음".equals(fcstValue)) {
            return 0.0;
        }
        return fcstValue.endsWith("mm") ? Double.parseDouble(fcstValue.replace("mm", "").trim()) : 0.0;
    }

    private WeatherForecastPerDayDTO buildWeatherForecastPerDayDTO(double minTemp, double maxTemp, double humidity, double rainFall, double temp, String stadiumLocation, Baseball baseball) {
        return WeatherForecastPerDayDTO.builder()
                .minTemp(minTemp)
                .maxTemp(maxTemp)
                .humidity(humidity)
                .rainFall(rainFall)
                .temp(temp)
                .sky(getWeatherForecastDataWithGame(baseball))
                .skyUrl(getWeatherUrl(getWeatherForecastDataWithGame(baseball)))
                .stadium(stadiumLocation)
                .build();
    }

    /**
     * 경기 카드에 들어갈 날씨 조회
     * @param game
     * @return
     */
    public WeatherForecastEnum getWeatherForecastDataWithGame(Baseball game) {
        Stadium stadium = stadiumRepository.findTopByName(game.getLocation())
                .orElseThrow(() -> new IllegalArgumentException("Illegal Stadium Name"));

        LocalDateTime gameTime = game.getTime();

        WeatherForecast sky = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                        stadium.getNx(), stadium.getNy(), "SKY", gameTime)
                .orElse(null);

        WeatherForecast pty = weatherForecastRepository.findTopByNxAndNyAndCategoryAndFcstTimeIsAfter(
                        stadium.getNx(), stadium.getNy(), "PTY", gameTime)
                .orElse(null);

        if (sky == null || pty == null) {
            // No data found
            return null;
        }

        // 날씨 상태 결정
        if ("1".equals(sky.getFcstValue())) {
            return getWeatherForecastForClearSky(pty.getFcstValue());
        } else if ("3".equals(sky.getFcstValue())) {
            return getWeatherForecastForCloudySky(pty.getFcstValue());
        } else if ("4".equals(sky.getFcstValue())) {
            return getWeatherForecastForOvercastSky(pty.getFcstValue());
        } else {
            throw new IllegalStateException("Unexpected sky value");
        }
    }

    private WeatherForecastEnum getWeatherForecastForClearSky(String fcstValue) {
        switch (fcstValue) {
            case "0": return WeatherForecastEnum.SUNNY;
            case "1": return WeatherForecastEnum.RAINY;
            case "2":
            case "3": return WeatherForecastEnum.SNOW;
            default: return WeatherForecastEnum.SHOWER;
        }
    }

    private WeatherForecastEnum getWeatherForecastForCloudySky(String fcstValue) {
        switch (fcstValue) {
            case "0": return WeatherForecastEnum.CLOUDY;
            case "1": return WeatherForecastEnum.RAINY;
            case "2":
            case "3": return WeatherForecastEnum.SNOW;
            default: return WeatherForecastEnum.SHOWER;
        }
    }

    private WeatherForecastEnum getWeatherForecastForOvercastSky(String fcstValue) {
        switch (fcstValue) {
            case "0": return WeatherForecastEnum.CLOUDY;
            case "1": return WeatherForecastEnum.RAINY;
            case "2":
            case "3": return WeatherForecastEnum.SNOW;
            default: return WeatherForecastEnum.SHOWER;
        }
    }

    /**
     * 날씨 데이터 수집
     * @param baseDate
     * @param baseTime
     * @param nx
     * @param ny
     * @throws IOException
     */
    @Transactional
    public void fetchAndSaveShortTermForecastData(String baseDate, String baseTime, int nx, int ny) throws IOException {
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
