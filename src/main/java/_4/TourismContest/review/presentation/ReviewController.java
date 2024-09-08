package _4.TourismContest.review.presentation;


import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.review.application.ReviewService;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
import _4.TourismContest.review.dto.request.ReviewUpdateRequest;
import _4.TourismContest.review.dto.response.ReviewPreviewsResponse;
import _4.TourismContest.review.dto.response.ReviewsResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/{contentId}")
    @Operation(summary = "리뷰 작성하는 api" ,description = "contentId, 토큰, 리뷰 작성 내용 입력해주세요. ")
    public ResponseEntity<String> saveReview(@PathVariable("contentId") Long contentId,
                                       @CurrentUser UserPrincipal user,
                                       @RequestBody ReviewCreateRequest request) {
        reviewService.saveReview(user.getId(), contentId, request);
        return new ResponseEntity<>("success save review", HttpStatus.OK);
    }

    @GetMapping("/{contentId}")
    @Operation(summary = "리뷰 리스트 가져오는 api" ,description = "contentId, 토큰, 정렬 기준을 넣어주세요. 정렬 기준 : ('new' : 최신순), ('like' : 좋아요순), ('ole' : 오래된순)")
    public ResponseEntity<ReviewsResponse> getReviews(@PathVariable("contentId") Long contentId,
                                                      @CurrentUser UserPrincipal user,
                                                      @RequestParam String sort) {
        ReviewsResponse reviewsResponse = reviewService.getSpotReviews(contentId, user.getId(), sort);
        return new ResponseEntity<>(reviewsResponse, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정하는 api" ,description = "reviewId, 토큰, 리뷰 수정 내용 입력해주세요. ")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId,
                                               @CurrentUser UserPrincipal user,
                                                @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(reviewId, user.getId(),request);
        return new ResponseEntity<>("success update review", HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제하는 api" ,description = "reviewId, 토큰 입력해주세요. ")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId,
                                               @CurrentUser UserPrincipal user) {
        reviewService.deleteReview(reviewId, user.getId());
        return new ResponseEntity<>("success delete review", HttpStatus.OK);
    }

    @PatchMapping("/{reviewId}/like")
    @Operation(summary = "리뷰 좋아요 api" ,description = "reviewId, 토큰 입력해주세요. ")
    public ResponseEntity<String> likeReview(@PathVariable("reviewId") Long reviewId,
                                             @CurrentUser UserPrincipal user) {
        String result = reviewService.likeReview(reviewId, user.getId());
        return new ResponseEntity<>("success "+result+" review", HttpStatus.OK);
    }

    @GetMapping("/my-review")
    @Operation(summary = "내가 작성한 리뷰 리스트 가져오는 api" ,description = "토큰 입력해주세요. ")
    public ResponseEntity<ReviewPreviewsResponse> getReviews(@CurrentUser UserPrincipal user) {
        ReviewPreviewsResponse reviewsResponse = reviewService.getReviewsByUser(user.getId());
        return new ResponseEntity<>(reviewsResponse, HttpStatus.OK);
    }
}
