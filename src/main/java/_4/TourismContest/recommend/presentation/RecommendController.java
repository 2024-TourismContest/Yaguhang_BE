package _4.TourismContest.recommend.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.application.RecommendService;
import _4.TourismContest.recommend.dto.event.RecommendPreviewResponse;
import _4.TourismContest.spot.dto.event.spotDetailResponse.SpotDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;
    @GetMapping("/{stadiumId}/{pageSize}")
    @Operation(summary = "유저가 추천하는 놀거리를 좋아요 순으로 가져오는 api" ,description = "-")
    public ResponseEntity<RecommendPreviewResponse> getStadiumSpot(@PathVariable Long stadiumId, @PathVariable Integer pageSize,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getRecommendList(stadiumId, pageSize, userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse, HttpStatus.OK);
    }
}
