package _4.TourismContest.baseball.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseBallDTO {
    private Long id;
    private String home;
    private String away;
    private String stadium;
    private String date;
    private String time;
    private String weather;
    private Boolean isScraped;
}
