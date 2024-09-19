package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewPreviewDto(
        Long spotId,
        Long reviewId,
        float star,
        int likeCount,
        String image,
        LocalDateTime createdAt,
        String content,
        String spotName
) {
    public static ReviewPreviewDto of(Review review, String imageUrl) {
        return new ReviewPreviewDto(
                review.getSpot().getId(),
                review.getId(),
                review.getStar(),
                review.getLikeCount(),
                imageUrl,
                review.getCreatedAt(),
                review.getContent(),
                review.getSpot().getName()
        );
    }
}
