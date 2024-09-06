package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendPreviewDto(
        Long recommendId,
        String stadiumName,
        String authorName,
        String profileImage,
        String title,
        LocalDateTime createdAt,
        List<String> images,
        Integer likes,
        Boolean isMine,
        Boolean isLiked
) {
}
