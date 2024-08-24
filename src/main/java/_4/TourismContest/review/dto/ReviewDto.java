package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewDto(
        Long reviewId,
        Long reviewerId,
        float star,
        int likeCount,
        LocalDateTime createdAt
) {
    public static ReviewDto of(Review review){
        return new ReviewDto(
                review.getId(),
                review.getUser().getId(),
                review.getStar(),
                review.getLikeCount(),
                review.getCreatedAt()
        );
    }

    public static List<ReviewDto> of(List<Review> reviews){
        return reviews.stream()
                .map(ReviewDto::of)
                .collect(Collectors.toList());
    }
}
