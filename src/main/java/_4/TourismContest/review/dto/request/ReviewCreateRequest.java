package _4.TourismContest.review.dto.request;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public record ReviewCreateRequest(
        float star,
        String content,
        String stadiumName,
        List<String> images
) {
    public Review toReviewEntity(User user, Spot spot){
        return Review.builder()
                .user(user)
                .spot(spot)
                .star(this.star)
                .content(this.content)
                .build();
    }

    public List<ReviewImage> toReviewImageEntities(Review review){
        return this.images.stream()
                .map(image -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(image)
                        .build())
                .collect(Collectors.toList());
    }
}
