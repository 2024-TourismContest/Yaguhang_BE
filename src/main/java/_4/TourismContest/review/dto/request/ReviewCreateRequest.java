package _4.TourismContest.review.dto.request;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.user.domain.User;

public record ReviewCreateRequest(
        float star,
        String content
) {
    public Review toEntity(User user, Spot spot){
        return Review.builder()
                .user(user)
                .spot(spot)
                .star(this.star)
                .content(this.content)
                .build();
    }
}
