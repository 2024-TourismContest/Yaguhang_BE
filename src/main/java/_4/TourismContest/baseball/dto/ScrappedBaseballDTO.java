package _4.TourismContest.baseball.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ScrappedBaseballDTO {
    private Integer pageIndex;
    private Integer pageSize;
    private List<BaseBallDTO> scrappedSchedules;

    @Builder
    public ScrappedBaseballDTO(Integer pageIndex, Integer pageSize, List<BaseBallDTO> scrappedSchedules){
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.scrappedSchedules = scrappedSchedules;
    }

}
