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

    @PatchMapping("/spot/{contentId}/{stadiumId}")
    @Operation(summary = "주변 볼거리 스크랩하기")
    public ResponseEntity<String> scrapSpot(@PathVariable Long contentId, @PathVariable Long stadiumId, @CurrentUser UserPrincipal userPrincipal){
        return new ResponseEntity<>(spotService.scrapSpot(contentId, stadiumId, userPrincipal), HttpStatus.OK);
    }

    @GetMapping("/spot")
    @Operation(summary = "내가 스크랩한 스크랩 가져오기" , description = "토큰만 보내면 됩니다.")
    public ResponseEntity<ScrapResponseDto> scrapSpot(@CurrentUser UserPrincipal userPrincipal){
        ScrapResponseDto scrapResponseDto = spotService.getMyScrap(userPrincipal);
        return new ResponseEntity<>(scrapResponseDto, HttpStatus.OK);
    }
}
