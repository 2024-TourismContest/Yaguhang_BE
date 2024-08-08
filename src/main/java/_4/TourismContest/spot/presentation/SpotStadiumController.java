package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.dto.event.SpotMapResponseDto;
import _4.TourismContest.spot.dto.event.SpotStadiumPreviewResponse;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/stadium")
@RequiredArgsConstructor
public class SpotStadiumController {
    private final SpotService spotService;

    @GetMapping("/{stadium}/{category}/{pagesize}/{pageindex}")
    @Operation(summary = "구장별 보기 페이지 기본 카테고리 조회" ,description = "구장, 카테고리 필터를 사용하여, 조회. 페이지 사이즈 지정하여 원하는 데이터 개수 선택 가능합니다. 토큰과 같이 보낼 시 스크랩 여부도 같이 감. 카테고리 필터 : (숙소, 맛집, 문화, 쇼핑)")
    public ResponseEntity<SpotStadiumPreviewResponse> getStadiumSpot(@PathVariable String stadium, @PathVariable String category,
                                                                     @PathVariable Integer pagesize, @PathVariable Integer pageindex, @CurrentUser UserPrincipal userPrincipal) {
        SpotStadiumPreviewResponse spotStadiumPreviewResponse = spotService.getStadiumSpot(stadium, category, pagesize, pageindex, userPrincipal);
        return new ResponseEntity<>(spotStadiumPreviewResponse, HttpStatus.OK);
    }

    @GetMapping("/{stadium}/{category}/{level}/{nowX}/{nowY}")
    @Operation(summary = "구장 별 주변관광지에 대한 마커를 찍기 위한 정보 ", description = "구장, 카테고리 필터를 사용하여, 구장 근처의 각 카테고리에 맞는 주변 볼거리 정보를 제공합니다. \n" +
            "radius: (km 단위)")
    public ResponseEntity<List<SpotMapResponseDto>> getStadiumNearSpot(@PathVariable String stadium, @PathVariable String category, @PathVariable int level,
                                                       @PathVariable double nowX, @PathVariable double nowY, @CurrentUser UserPrincipal userPrincipal) throws IOException {
        List<SpotMapResponseDto> nearSpot = spotService.getNearSpot(nowX, nowY, stadium, category, level, userPrincipal);
        return ResponseEntity.ok(nearSpot);
    }
}
