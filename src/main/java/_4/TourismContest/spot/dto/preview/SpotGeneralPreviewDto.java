package _4.TourismContest.spot.dto.preview;

import _4.TourismContest.spot.domain.SpotCategory;
import lombok.Builder;

@Builder
public record SpotGeneralPreviewDto(
        Long contentId,
        String name,
        String address,
        String imageUrl,
        Boolean isScraped,
        String category,
        String categoryUrl
) implements SpotBasicPreviewDto{
}
