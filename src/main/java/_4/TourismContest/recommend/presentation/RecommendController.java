package _4.TourismContest.recommend.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.application.RecommendService;
import _4.TourismContest.recommend.dto.event.RecommendPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;
    @GetMapping("")
    @Operation(summary = "유저가 추천하는 놀거리를 좋아요 순으로 가져오는 api" ,description = "-")
    public ResponseEntity<RecommendPreviewResponse> getStadiumSpot(@RequestParam Long stadiumId,
                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getRecommendList(stadiumId, pageSize, userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse, HttpStatus.OK);
    }
}
