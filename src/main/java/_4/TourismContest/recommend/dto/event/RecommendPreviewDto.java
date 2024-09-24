package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record RecommendPreviewDto(
        Long recommendId,

        Long stadiumId,
        String stadiumImage,
        String stadiumName,

        String authorName,
        String profileImage,
        String likeTeam,
        String likeTeamUrl,

        String title,
        String createdAt,
        String description,
        List<String> images,
        Integer likes,
        Boolean isMine,
        Boolean isLiked
) {
}
