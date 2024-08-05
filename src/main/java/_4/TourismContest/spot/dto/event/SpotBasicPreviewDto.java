package _4.TourismContest.spot.dto.event;

import lombok.Builder;

@Builder
public record SpotBasicPreviewDto(
        Long contentId,
        String name,
        String address,
        String imageUrl,
        Boolean isScraped
) {
}
