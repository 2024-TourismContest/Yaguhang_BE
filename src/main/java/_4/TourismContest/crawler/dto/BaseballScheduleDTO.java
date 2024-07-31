package _4.TourismContest.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
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
