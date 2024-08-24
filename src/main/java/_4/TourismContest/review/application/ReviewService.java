package _4.TourismContest.review.application;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.dto.ReviewDto;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
import _4.TourismContest.review.dto.request.ReviewUpdateRequest;
import _4.TourismContest.review.dto.response.ReviewsResponse;
import _4.TourismContest.review.repository.ReviewRepository;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    public void saveReview(Long userId, Long spotId, ReviewCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("no user"));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("no spot"));

        Review review = request.toEntity(user, spot);

        reviewRepository.save(review);
    }

    public ReviewsResponse getSpotReviews(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("no spot"));

        List<Review> reviews = reviewRepository.findAllBySpot(spot);
        List<ReviewDto> reviewDtos = ReviewDto.of(reviews);
        return ReviewsResponse.of(reviewDtos);
    }

    public void updateReview(Long reviewId, Long userId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("no review"));
        User author = review.getUser();
        
        if(author.getId()==userId){
            Review updatedReview = review.update(request);
            reviewRepository.save(updatedReview);
        }
        else{
            throw new IllegalArgumentException("not author");
        }
    }

    public void deleteReview(Long reviewId, Long userId) {
    }
}
