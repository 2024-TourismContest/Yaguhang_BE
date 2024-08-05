package _4.TourismContest.baseball.presentation;

import _4.TourismContest.baseball.application.BaseballScrapService;
import _4.TourismContest.baseball.dto.BaseBallScrapResponseDTO;
import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class BaseballScrapController {
    private final BaseballScrapService baseballScrapService;

    @PatchMapping("/schedule/create")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "야구 경기 스크랩하기")
    public ResponseEntity<BaseBallScrapResponseDTO> createScrap(@CurrentUser UserPrincipal userPrincipal, Long gameId){
        BaseBallScrapResponseDTO scrap = baseballScrapService.createScrap(userPrincipal, gameId);
        return ResponseEntity.ok(scrap);
    }

    @PatchMapping("/schedule/delete")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "스크랩 되어 있는 경기 스크랩 취소하기")
    public ResponseEntity<BaseBallScrapResponseDTO> deleteScrap(@CurrentUser UserPrincipal userPrincipal, Long gameId){
        BaseBallScrapResponseDTO scrap = baseballScrapService.deleteScrap(userPrincipal, gameId);
        return ResponseEntity.ok(scrap);
    }
}
