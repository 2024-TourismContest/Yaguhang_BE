package _4.TourismContest.spot.dto.event;

import lombok.Builder;

@Builder
public record SpotMapResponseDto(
        Double mapX,
        Double mapY,
        Integer contentId,
        String name,
        boolean isScrapped
) {
}
