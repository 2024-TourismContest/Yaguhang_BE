package _4.TourismContest.spot.dto.preview;

import lombok.Builder;

@Builder
public record SpotGeneralPreviewDto(
        Long contentId,
        String name,
        String address,
        String imageUrl,
        Boolean isScraped
) implements SpotBasicPreviewDto{
}
