package _4.TourismContest.baseball.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseballScheduleDTO {
    private String team;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String date;

    private Integer pageIndex;

    private List<BaseBallDTO> schedules;
}
