package _4.TourismContest.spot.dto.event;

import lombok.Builder;

@Builder
public record SpotDetailInfoDto(
        Long contentId,
        Long stadiumId,
        boolean isScraped,
        String title,
        String address,
        Double mapX,
        Double mapY,
        String image,
        String description,
        int reviewCount
) {
}
