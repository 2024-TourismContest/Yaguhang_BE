package _4.TourismContest.recommend.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.application.RecommendService;
import _4.TourismContest.recommend.dto.command.RecommendPostRequest;
import _4.TourismContest.recommend.dto.event.RecommendDetailResponse;
import _4.TourismContest.recommend.dto.event.RecommendPreviewResponse;
import _4.TourismContest.recommend.dto.event.RecommendSpotScrapResponse;
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
    @Operation(summary = "추천행 리스트를 좋아요 순으로 가져오는 api" ,description = "경기장 이름과 pagesige 넣어주세요")
    public ResponseEntity<RecommendPreviewResponse> getRecommend(@RequestParam String stadiumName,
                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getRecommendList(stadiumName, pageSize, userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse, HttpStatus.OK);
    }

    @GetMapping("/detail")
    @Operation(summary = "추천행 상세 페이지 정보 가져오는 api" ,description = "추천행 id 넣어주세요")
    public ResponseEntity<RecommendDetailResponse> getRecommendDetail(@RequestParam Long recommendId,
                                                                  @CurrentUser UserPrincipal userPrincipal) {
        RecommendDetailResponse recommendDetailResponse = recommendService.getRecommendDetail(recommendId, userPrincipal);
        return new ResponseEntity<>(recommendDetailResponse, HttpStatus.OK);
    }

    @PatchMapping("/like")
    @Operation(summary = "추천행 좋아요 api" ,description = "추천행 id 넣어주세요. 토큰 필수!")
    public ResponseEntity<String> addRecommendLike(@RequestParam Long recommendId,
                                                                  @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(recommendService.scrapRecommend(recommendId, userPrincipal), HttpStatus.OK);
    }

    @GetMapping("/myscrap")
    @Operation(summary = "추천행 작성 시 내가 스크랩한 목록 api" ,description = "stadium 이름 넣어주세요, 토큰 필수!")
    public ResponseEntity<RecommendSpotScrapResponse> getScrapList(@RequestParam String stadiumName,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendSpotScrapResponse recommendSpotScrapResponse = recommendService.getrecommendSpotScrapResponse(stadiumName, userPrincipal);
        return new ResponseEntity<>(recommendSpotScrapResponse, HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "추천행 작성 api" ,description = "토큰 필수!")
    public ResponseEntity<String> postRecommend(@RequestBody RecommendPostRequest recommendPostRequest,
                                               @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(recommendService.postRecommend(recommendPostRequest, userPrincipal), HttpStatus.OK);
    }

    @DeleteMapping("")
    @Operation(summary = "추천행 삭제 api" ,description = "토큰 필수!")
    public ResponseEntity<String> deleteRecommend(@RequestParam Long recommendId,
                                                @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(recommendService.deleteRecommend(recommendId, userPrincipal), HttpStatus.OK);
    }

    @GetMapping("/myrecommend")
    @Operation(summary = "내가 작성한 추천행 리스트를 좋아요 순으로 가져오는 api" ,description = "토큰과 pagesige 넣어주세요")
    public ResponseEntity<RecommendPreviewResponse> getMyRecommend(@RequestParam(defaultValue = "10") Integer pageSize,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getMyRecommendList(pageSize, userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse, HttpStatus.OK);
    }
}
