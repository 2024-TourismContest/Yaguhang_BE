package _4.TourismContest.review.application;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;
import _4.TourismContest.review.domain.ReviewLike;
import _4.TourismContest.review.dto.ReviewDto;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
import _4.TourismContest.review.dto.request.ReviewUpdateRequest;
import _4.TourismContest.review.dto.response.ReviewsResponse;
import _4.TourismContest.review.repository.ReviewImageRepository;
import _4.TourismContest.review.repository.ReviewLikeRepository;
import _4.TourismContest.review.repository.ReviewRepository;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService { //CUD와 R 서비스의 분리가 필요해 보임
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    public void saveReview(Long userId, Long spotId, ReviewCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("no user"));

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("no spot"));

        Review review = request.toReviewEntity(user, spot);
        reviewRepository.save(review);

        List<ReviewImage> reviewImages = request.toReviewImageEntities(review);
        reviewImageRepository.saveAll(reviewImages);
    }

    public ReviewsResponse getSpotReviews(Long spotId, String sort) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("no spot"));

        List<Review> reviews;
        if(sort.equals("new")){
            reviews = reviewRepository.findAllBySpotOrderByIdAsc(spot);
        }
        else if(sort.equals("like")){
            reviews = reviewRepository.findAllBySpotOrderByLikeCountIdDesc(spot);
        }
        else if(sort.equals("old")){
            reviews = reviewRepository.findAllBySpotOrderByIdDesc(spot);
        }
        else{
            throw new IllegalArgumentException("not accestable sort by");
        }

        List<ReviewDto> reviewDtos = new ArrayList<>();
        for(Review review : reviews){
            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(review);
            reviewDtos.add(ReviewDto.of(review, reviewImages));
        }
        return ReviewsResponse.of(reviewDtos);
    }

    public void updateReview(Long reviewId, Long userId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("no review"));
        User author = review.getUser();
        
        if(author.getId()==userId){
            Review updatedReview = review.update(request);
            reviewRepository.save(updatedReview);

            //일급 컬렉션이 필요함을 느낌..
            List<ReviewImage> reviewImages = reviewImageRepository.findAllByReview(updatedReview);
            reviewImageRepository.deleteAll(reviewImages);

            List<String> images = request.images();
            List<ReviewImage> updatedReviewImages = images.stream()
                    .map(image -> ReviewImage.builder()
                            .id(updatedReview.getId())
                            .review(updatedReview)
                            .imageUrl(image)
                            .build())
                    .collect(Collectors.toList());
            reviewImageRepository.saveAll(updatedReviewImages);
        }
        else{
            throw new IllegalArgumentException("not author");
        }
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("no review"));
        User author = review.getUser();

        if(author.getId()==userId){
            reviewRepository.delete(review);
        }
        else{
            throw new IllegalArgumentException("not author");
        }
    }

    public String likeReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("no review"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("no user"));

        Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByUserAndReview(user, review);
        if(reviewLikeOptional.isPresent()){
            review.subLikesCount();
            reviewRepository.save(review);

            reviewLikeRepository.delete(reviewLikeOptional.get());
            return "remove";
        }
        else{
            review.addLikesCount();
            reviewRepository.save(review);

            ReviewLike reviewLike = ReviewLike.builder()
                    .user(user)
                    .review(review)
                    .build();
            reviewLikeRepository.save(reviewLike);
            return "add";
        }
    }
}
