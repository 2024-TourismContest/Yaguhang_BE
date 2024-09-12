package _4.TourismContest.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewScrapResponse(
        String message,
        Integer likeCount
) {
}
