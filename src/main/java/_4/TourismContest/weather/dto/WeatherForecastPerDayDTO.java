package _4.TourismContest.weather.dto;

import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecastPerDayDTO {
    private double minTemp;
    private double maxTemp;
    private double humidity;
    private double temp;
    private double rainFall;
    private WeatherForecastEnum sky;
    private String skyUrl;
    private String stadium;
}
