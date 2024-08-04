package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class SpotMainController {
    private final SpotService spotService;

    @GetMapping("/place/{stadium}/{category}")
    @Operation(summary = "메인페이지의 구장 주변 관광지 추천" ,description = "구장, 카테고리 필터를 사용하여 조회. 토큰과 같이 보낼 시 스크랩 여부도 같이 감.")
    public ResponseEntity<SpotCategoryResponse> getMainSpot(@PathVariable String stadium, @PathVariable String category, @CurrentUser UserPrincipal userPrincipal) {
        SpotCategoryResponse spotCategoryResponse = spotService.getMainSpot(stadium, category, userPrincipal);
        return new ResponseEntity<>(spotCategoryResponse, HttpStatus.OK);
    }

}
