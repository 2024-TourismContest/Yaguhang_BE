package _4.TourismContest.spot.dto.event;

import lombok.Builder;

@Builder
public record SpotMapResponseDto(
        Long contentId,
        Long stadiumId,
        String title,
        String address,
        Double mapX,
        Double mapY,
        String image,
        String description,
        Long reviewCount,
        boolean isScrapped
) {
}

