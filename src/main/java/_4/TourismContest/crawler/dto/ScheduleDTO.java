package _4.TourismContest.crawler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleDTO {
    private String day;
    private String time;
    private String homeTeam;
    private String vs;
    private String awayTeam;
    private String location;
}
