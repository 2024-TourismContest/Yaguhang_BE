package _4.TourismContest.crawler.dto;

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
