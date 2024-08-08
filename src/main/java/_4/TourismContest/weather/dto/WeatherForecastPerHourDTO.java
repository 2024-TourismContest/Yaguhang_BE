package _4.TourismContest.weather.dto;

import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherForecastPerHourDTO {
    private String fcstDate;    //예측 날짜
    private String fcstTime;    //예측 시간
    private WeatherForecastEnum weatherForecast;    //하늘 상태
    private String weatherImgUrl;   //날씨 아이콘 URL
    private int rainyPercent;   //강수확률
    private int temp;   //온도
}
