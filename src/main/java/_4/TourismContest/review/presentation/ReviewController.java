package _4.TourismContest.review.presentation;


import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.review.application.ReviewService;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
import _4.TourismContest.review.dto.request.ReviewUpdateRequest;
import _4.TourismContest.review.dto.response.ReviewPreviewsResponse;
import _4.TourismContest.review.dto.response.ReviewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{spotId}")
    public ResponseEntity<String> saveReview(@PathVariable("spotId") Long spotId,
                                       @CurrentUser UserPrincipal user,
                                       @RequestBody ReviewCreateRequest request) {
        reviewService.saveReview(user.getId(), spotId, request);
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
                                               @CurrentUser UserPrincipal user,
                                                @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(reviewId, user.getId(),request);
        return new ResponseEntity<>("success update review", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                               @CurrentUser UserPrincipal user) {
        reviewService.deleteReview(reviewId, user.getId());
        return new ResponseEntity<>("success delete review", HttpStatus.OK);
    }

    @PatchMapping("/{reviewId}/like")
    public ResponseEntity<String> likeReview(@PathVariable("reviewId") Long reviewId,
                                             @CurrentUser UserPrincipal user) {
        String result = reviewService.likeReview(reviewId, user.getId());
        return new ResponseEntity<>("success "+result+" review", HttpStatus.OK);
    }

    @GetMapping("/my-review")
    public ResponseEntity<ReviewPreviewsResponse> getReviews(@CurrentUser UserPrincipal user) {
        ReviewPreviewsResponse reviewsResponse = reviewService.getReviewsByUser(user.getId());
        return new ResponseEntity<>(reviewsResponse, HttpStatus.OK);
    }
}
