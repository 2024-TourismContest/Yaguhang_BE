package _4.TourismContest.baseball.dto;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class ScheduleMeta {
    LocalDateTime gameTime;
    String homeTeam;
    String awayTeam;
    String location;
    String status;
    int homeScore;
    int awayScore;
    String homePitcher;
    String awayPitcher;
}