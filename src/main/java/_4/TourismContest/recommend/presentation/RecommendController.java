package _4.TourismContest.recommend.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.application.RecommendService;
import _4.TourismContest.recommend.dto.command.RecommendPostRequest;
import _4.TourismContest.recommend.dto.event.RecommendDetailResponse;
import _4.TourismContest.recommend.dto.event.RecommendPreviewResponse;
import _4.TourismContest.recommend.dto.event.RecommendScrapResponse;
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
    @Operation(summary = "추천행 리스트 가져오는 api" ,description = "pageIndex, pagesige 넣어주세요. 정렬 기준 : '인기순', '최신순'.  필터 : 전체 , 사직, 고척...")
    public ResponseEntity<RecommendPreviewResponse> getRecommend(@RequestParam(defaultValue = "0") Integer pageIndex,
                                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                                   @RequestParam(defaultValue = "인기순") String order,
                                                                   @RequestParam(defaultValue = "전체")String filter,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getRecommendList(pageIndex, pageSize, order, filter,userPrincipal);
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
    public ResponseEntity<RecommendScrapResponse> addRecommendLike(@RequestParam Long recommendId,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(recommendService.likeRecommend(recommendId, userPrincipal), HttpStatus.OK);
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
    @Operation(summary = "내가 작성한 추천행 리스트를 가져오는 api" ,description = "토큰과 pagesige 넣어주세요")
    public ResponseEntity<RecommendPreviewResponse> getMyRecommend(@RequestParam(defaultValue = "10") Integer pageSize,
                                                                   @CurrentUser UserPrincipal userPrincipal) {
        RecommendPreviewResponse recommendPreviewResponse = recommendService.getMyRecommendList(pageSize, userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse, HttpStatus.OK);
    }
    @GetMapping("/search")
    @Operation(summary = "키워드로 추천행을 검색하는 API", description = "키워드를 입력할 경우, 키워드가 포함된 추천행 리스트가 반환됩니다.")
    public ResponseEntity<RecommendPreviewResponse> searchRecommendList(@RequestParam(defaultValue = "0") Integer pageIndex,
                                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                                        @RequestParam(defaultValue = "인기순") String order,
                                                                        @RequestParam(defaultValue = "전체") String filter,
                                                                        @RequestParam String keyWord,
                                                                        @CurrentUser UserPrincipal userPrincipal){
        RecommendPreviewResponse recommendPreviewResponse = recommendService.searchRecommendListByKeyword(pageIndex,pageSize,keyWord,order,filter,userPrincipal);
        return new ResponseEntity<>(recommendPreviewResponse,HttpStatus.OK);
    }
}
