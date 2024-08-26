package _4.TourismContest.review.presentation;


import _4.TourismContest.review.application.ReviewService;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
import _4.TourismContest.review.dto.request.ReviewUpdateRequest;
import _4.TourismContest.review.dto.response.ReviewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{spotId}")
    public ResponseEntity<String> saveReview(@PathVariable("spotId") Long spotId,
                                       @RequestParam Long userId,
                                       @RequestBody ReviewCreateRequest request) {
        reviewService.saveReview(userId, spotId, request);
        return new ResponseEntity<>("success save review", HttpStatus.OK);
    }

    @GetMapping("/{spotId}")
    public ResponseEntity<ReviewsResponse> getReviews(@PathVariable("spotId") Long spotId,
                                                      @RequestParam String sort) {
        ReviewsResponse reviewsResponse = reviewService.getSpotReviews(spotId, sort);
        return new ResponseEntity<>(reviewsResponse, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId,
                                                @RequestParam Long userId,
                                                @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(reviewId, userId,request);
        return new ResponseEntity<>("success update review", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                       @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return new ResponseEntity<>("success delete review", HttpStatus.OK);
    }
}
