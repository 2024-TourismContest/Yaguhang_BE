package _4.TourismContest.review.presentation;


import _4.TourismContest.review.application.ReviewService;
import _4.TourismContest.review.dto.request.ReviewCreateRequest;
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
}
