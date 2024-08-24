package _4.TourismContest.review.dto.response;

import _4.TourismContest.review.dto.ReviewDto;

import java.util.List;

public record ReviewsResponse(
        List<ReviewDto> reviews
) {
    public static ReviewsResponse of(List<ReviewDto> reviewDtoList){
        return new ReviewsResponse(reviewDtoList);
    }
}
