package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.spotDetailResponse.SpotDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spot/detail")
@RequiredArgsConstructor
public class SpotDetailController {
    private final SpotService spotService;
    @GetMapping("")
    @Operation(summary = "상세페이지 api" ,description = "어떤 카테고리 인지 같이 보내야합니다. (숙소, 맛집, 문화, 쇼핑, 선수맛집)")
    public ResponseEntity<SpotDetailResponse> getStadiumSpot(@RequestParam String category,
                                                             @RequestParam Long contentId,
                                                             @RequestParam Long stadiumId,
                                                             @CurrentUser UserPrincipal userPrincipal) {
        SpotDetailResponse spotDetailResponse = spotService.getDetailSpot(category, contentId, stadiumId,userPrincipal);
        return new ResponseEntity<>(spotDetailResponse, HttpStatus.OK);
    }
}
