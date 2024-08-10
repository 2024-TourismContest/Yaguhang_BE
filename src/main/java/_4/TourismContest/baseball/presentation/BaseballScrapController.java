package _4.TourismContest.baseball.presentation;

import _4.TourismContest.baseball.application.BaseballScrapService;
import _4.TourismContest.baseball.application.BaseballService;
import _4.TourismContest.baseball.dto.BaseBallScrapResponseDTO;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import _4.TourismContest.baseball.dto.ScrappedBaseballDTO;
import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class BaseballScrapController {
    private final BaseballScrapService baseballScrapService;

    @PatchMapping("/schedule/scrap")
    @Operation(summary = "야구 경기 스크랩하기")
    public ResponseEntity<String> scrapSchdule(@CurrentUser UserPrincipal userPrincipal, Long gameId){
        return new ResponseEntity<>(baseballScrapService.scrapSchdule(userPrincipal, gameId), HttpStatus.OK);
    }


    @GetMapping("/schedule")
    @Operation(summary = "스크랩 한 경기 일정 조회하기", description = "내가 스크랩한 경기 일정 가져오기")
    public ResponseEntity<ScrappedBaseballDTO> getScrappedBaseballGame(@CurrentUser UserPrincipal userPrincipal, int page, int size){
        ScrappedBaseballDTO scrappedBaseballGamesList = baseballScrapService.getScrappedBaseballGamesList(userPrincipal, page, size);
        return ResponseEntity.ok(scrappedBaseballGamesList);
    }
}
