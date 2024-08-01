package _4.TourismContest.baseball.presentation;

import _4.TourismContest.baseball.application.BaseballService;
import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/")
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
    public List<Baseball> getSchedule() {
        return baseballService.scrapeAllSchedule();
    }
//    // 특정 날짜 조회
//    @GetMapping("/update2")
//    public void getSchedule2() {
//        crawlingService.scrapeTodayGame();
//    }

}