package _4.TourismContest.recommend.dto.event;

import _4.TourismContest.spot.dto.preview.SpotGeneralPreviewDto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendDetailResponse(
        	Long recommendId,
            String authorName,
            String profileImage,
            String title,
            LocalDateTime createdAt,
            Integer likes,
            Boolean isMine,
            Boolean isLiked,
            List<SpotGeneralPreviewDto> spotGeneralPreviewDtos
) {
}
