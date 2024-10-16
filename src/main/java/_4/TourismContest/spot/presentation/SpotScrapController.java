package _4.TourismContest.spot.presentation;


import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.command.ScrapResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class SpotScrapController {
    private final SpotService spotService;

    @PatchMapping("/spot")
    @Operation(summary = "주변 볼거리 스크랩하기")
    public ResponseEntity<String> scrapSpot(@RequestParam Long contentId,
                                            @RequestParam Long stadiumId,
                                            @CurrentUser UserPrincipal userPrincipal){
        return new ResponseEntity<>(spotService.scrapSpot(contentId, stadiumId, userPrincipal), HttpStatus.OK);
    }

//    @GetMapping("/spot")
//    @Operation(summary = "내가 스크랩한 스크랩 가져오기" , description = "토큰만 보내면 됩니다.")
//    public ResponseEntity<ScrapResponseDto> scrapSpot(@CurrentUser UserPrincipal userPrincipal){
//        ScrapResponseDto scrapResponseDto = spotService.getMyScrap(userPrincipal);
//        return new ResponseEntity<>(scrapResponseDto, HttpStatus.OK);
//    }

    @GetMapping("/spot/filter")
    @Operation(summary = "내가 스크랩한 스크랩 필터로 가져오기" , description = "pageIndex, pagesige 넣어주세요. 토큰 필수, 필터 : 전체 , 사직, 고척...")
    public ResponseEntity<ScrapResponseDto> scrapSpotByFilter(@RequestParam(defaultValue = "0") Integer pageIndex,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam(defaultValue = "전체") String filter,
                                                              @CurrentUser UserPrincipal userPrincipal){
        ScrapResponseDto scrapResponseDto = spotService.getMyScrap(pageIndex, pageSize, filter,userPrincipal);
        return new ResponseEntity<>(scrapResponseDto, HttpStatus.OK);
    }
}
