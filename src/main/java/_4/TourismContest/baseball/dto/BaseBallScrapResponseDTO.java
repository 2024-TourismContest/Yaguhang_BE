package _4.TourismContest.baseball.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseBallScrapResponseDTO {
    private Long gameId;
    private boolean isScrapped;

    @Builder
    public BaseBallScrapResponseDTO(Long gameId, boolean isScrapped){
        this.gameId = gameId;
        this.isScrapped = isScrapped;
    }
}
