package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewDto(
        Long reviewId,
        Long reviewerId,
        float star,
        int likeCount,
        LocalDateTime createdAt,
        String content,
        List<String> images
) {
    public static ReviewDto of(Review review, List<ReviewImage> reviewImages){
        return new ReviewDto(
                review.getId(),
                review.getUser().getId(),
                review.getStar(),
                review.getLikeCount(),
                review.getCreatedAt(),
                review.getContent(),
                reviewImages.stream()
                        .map(r -> r.getImageUrl())
                        .collect(Collectors.toList())
        );
    }
}
