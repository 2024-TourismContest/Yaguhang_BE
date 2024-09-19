package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewPreviewDto(
        Long stadiumId,
        Long spotId,
        String spotName,
        Long reviewId,
        float star,
        int likeCount,
        String image,
        LocalDateTime createdAt,
        String content,
        boolean isLiked
) {
    public static ReviewPreviewDto of(Review review, String imageUrl, boolean isLiked) {
        return new ReviewPreviewDto(
                review.getSpot().getStadium().getId(),
                review.getSpot().getId(),
                review.getSpot().getName(),
                review.getId(),
                review.getStar(),
                review.getLikeCount(),
                imageUrl,
                review.getCreatedAt(),
                review.getContent(),
                isLiked
        );
    }
}
