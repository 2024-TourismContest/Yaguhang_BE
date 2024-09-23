package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewPreviewDto(
        Long stadiumId,
        Long spotId,
        String spotName,
        Long reviewId,
        float star,
        int likeCount,
        List<String> image,
        LocalDateTime createdAt,
        String content,
        boolean isLiked,
        String category
) {
    public static ReviewPreviewDto of(Review review, List<ReviewImage> images, boolean isLiked, String category) {
        return new ReviewPreviewDto(
                review.getSpot().getStadium().getId(),
                review.getSpot().getId(),
                review.getSpot().getName(),
                review.getId(),
                review.getStar(),
                review.getLikeCount(),
                images.stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()),
                review.getCreatedAt(),
                review.getContent(),
                isLiked,
                category
        );
    }
}
