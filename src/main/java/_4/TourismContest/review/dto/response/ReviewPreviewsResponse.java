package _4.TourismContest.review.dto.response;

import _4.TourismContest.review.dto.ReviewPreviewDto;

import java.util.List;

public record ReviewPreviewsResponse(
        List<ReviewPreviewDto> reviews
) {
    public static ReviewPreviewsResponse of(List<ReviewPreviewDto> reviewPreviewDtos) {
        return new ReviewPreviewsResponse(reviewPreviewDtos);
    }
}
