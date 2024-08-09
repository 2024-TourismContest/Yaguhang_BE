package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RecommendPreviewDto(
        Long recommendId,
        String authorName,
        String profileImage,
        String title,
        LocalDateTime createdAt,
        String image,
        Integer likes,
        Boolean isMine,
        Boolean isLiked
) {
}
