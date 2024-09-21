package _4.TourismContest.stadium.dto;

import lombok.Builder;

@Builder
public record StadiumInfo(
        Long StadiumId,
        String StadiumImage,
        String teamName,
        String StadiumName
) {
}
