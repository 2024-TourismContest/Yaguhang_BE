package _4.TourismContest.recommend.dto.command;

import java.util.List;

public record RecommendPostRequest(
        String Stadium,
        String title,
        String description,
        List<Long> contentIdList
) {
}
