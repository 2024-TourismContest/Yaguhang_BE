package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

import java.util.List;
@Builder
public record RecommendPreviewResponse(
        Boolean hasNextPage,
        Integer pagesize,
        List<RecommendPreviewDto> recommendPreviewDtos
) {
}
