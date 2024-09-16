package _4.TourismContest.review.dto;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;
import _4.TourismContest.user.dto.event.UserInfoDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record ReviewDto(
        UserInfoDto user,
        Long reviewId,
        boolean isMine,
        boolean isLiked,
        float star,
        int likeCount,
        String createdAt,
        String content,
        List<String> images
) {
    public static String pattern = "yyyy-MM-dd HH:mm:ss";
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    public static ReviewDto of(Review review, List<ReviewImage> reviewImages, boolean isMine, boolean isLiked){
        return new ReviewDto(
                UserInfoDto.of(review.getUser()),
                review.getId(),
                isMine,
                isLiked,
                review.getStar(),
                review.getLikeCount(),
                review.getCreatedAt().format(dateTimeFormatter),
                review.getContent(),
                reviewImages.stream()
                        .map(r -> r.getImageUrl())
                        .collect(Collectors.toList())
        );
    }
}
