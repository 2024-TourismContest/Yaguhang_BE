package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

@Builder
public record RecommendScrapResponse(
        String message,
        Integer likeCount
) {
}
