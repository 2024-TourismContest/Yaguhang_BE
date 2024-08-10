package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.spotDetailResponse.SpotDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detail")
@RequiredArgsConstructor
public class SpotDetailController {
    private final SpotService spotService;
    @GetMapping("/{category}/{contentId}")
    @Operation(summary = "상세페이지 api" ,description = "어떤 카테고리 인지 같이 보내야합니다. (숙소, 맛집, 문화, 쇼핑)")
    public ResponseEntity<SpotDetailResponse> getStadiumSpot(@PathVariable String category, @PathVariable Long contentId,
                                                             @CurrentUser UserPrincipal userPrincipal) {
        SpotDetailResponse spotDetailResponse = spotService.getDetailSpot(category, contentId, userPrincipal);
        return new ResponseEntity<>(spotDetailResponse, HttpStatus.OK);
    }
}
