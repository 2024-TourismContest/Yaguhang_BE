package _4.TourismContest.crawler.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"date", "month", "home", "away", "time"})
@Table(name = "Baseball")
public class Baseball {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String time;
    private String home;
    private String away;
    private String location;
    private String status;
    @Column(nullable = true)
    private String homePitcher;
    @Column(nullable = true)
    private String awayPitcher;
    private int month;
    private int date;
    @Column(nullable = true)
    private int homeScore;
    @Column(nullable = true)
    private int awayScore;
    private String weekDay;
}
