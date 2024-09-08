package _4.TourismContest.weather.dto;

import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecastPerDayDTO {
    private Double minTemp;
    private Double maxTemp;
    private Double humidity;
    private Double temp;
    private Double rainFall;
    private WeatherForecastEnum sky;
    private String skyUrl;
    private String stadium;
}
