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
    private String team1;
    private String vs;
    private String team2;
    private String location;
}
