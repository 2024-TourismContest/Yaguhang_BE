package _4.TourismContest.crawler.domain;


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
//@EqualsAndHashCode(of = {"month", "home", "away", "time"})
@Table(name = "Baseball")
public class Baseball {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime time;
    private int month;
    private String weekDay;
    private String location;
    private String status;
    private String homePitcher;
    private String awayPitcher;
    private int homeScore;
    private int awayScore;
}
