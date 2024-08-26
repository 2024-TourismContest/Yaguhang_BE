package _4.TourismContest.review.dto.request;

import java.util.List;

public record ReviewUpdateRequest(
        float star,
        String content,
        List<String> images
) {
}
