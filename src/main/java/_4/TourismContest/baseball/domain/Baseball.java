package _4.TourismContest.baseball.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"home", "away", "time"})
@Table(name = "Baseball",
        uniqueConstraints= {
                @UniqueConstraint(
                        name = "home, away, time, location 조합은 단일이어야함",
                        columnNames = {
                                "home",
                                "away",
                                "time",
                                "location"
                        }
                )
        }
)
public class Baseball {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private String weekDay;
    private String home;
    private String away;
    private String location;
    private String status;
    private String homePitcher;
    private String awayPitcher;
    private int homeScore;
    private int awayScore;
}
