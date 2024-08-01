package _4.TourismContest.weather.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"fcstDate", "fcstTime"})
@Table(name = "Weather",
        uniqueConstraints= {
                @UniqueConstraint(
                        name = "category, fcstDate, fcstTime 조합은 단일이어야함",
                        columnNames = {
                                "nx",
                                "ny",
                                "fcstDate",
                                "fcstTime",
                                "category"
                        }
                )
        }
)
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private int nx;
    private int ny;
}