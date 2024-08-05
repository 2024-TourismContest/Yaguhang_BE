package _4.TourismContest.baseball.presentation;

import _4.TourismContest.baseball.application.BaseballService;
import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/")
    @Operation(summary = "각 팀별 경기 일정 가져오기",description = "조회하는 날짜 기준으로 각 팀별 경기 일정을 가져옵니다. (전체) 입력 가능")
    public ResponseEntity<BaseballScheduleDTO> getGames(
            @RequestParam String team,
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        // JWT 토큰 검증 (토큰 유효성 검증 코드 생략)
        BaseballScheduleDTO games = baseballService.getGamesByTeamAndDate(team, page, size);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/update")
    @Operation(summary = "경기 크롤링 기능 API 형태로 해놓은 것...사용X")
    public List<Baseball> getSchedule() {
        return baseballService.scrapeAllSchedule();
    }
//    // 특정 날짜 조회
//    @GetMapping("/update2")
//    public void getSchedule2() {
//        crawlingService.scrapeTodayGame();
//    }

}