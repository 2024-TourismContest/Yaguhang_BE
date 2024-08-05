package _4.TourismContest.baseball.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseBallSchedulePerMonthDTO {
    private String team;
    private List<LocalDate> dayOfGameIsNull;
}
