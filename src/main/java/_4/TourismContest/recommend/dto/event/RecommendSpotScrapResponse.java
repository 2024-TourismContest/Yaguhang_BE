package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendSpotScrapResponse(
        String stadium,
        List<ScrapAddressSpot> scrapAddressSpots
) {
}
