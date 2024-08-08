package _4.TourismContest.baseball.presentation;

import _4.TourismContest.baseball.application.BaseballService;
import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.dto.BaseBallSchedulePerMonthDTO;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class BaseballController {

    private final BaseballService baseballService;

    @GetMapping("/schedule/")
    @Operation(summary = "각 팀별 경기 일정 가져오기",description = "조회하는 날짜 기준으로 각 팀별 경기 일정을 가져옵니다. (전체) 입력 가능")
    public ResponseEntity<BaseballScheduleDTO> getGames(
            @RequestParam (defaultValue = "전체") String team,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam(required = false) LocalDate gameDate,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal userPrincipal
            ) {
        BaseballScheduleDTO games = baseballService.getGamesByTeamAndDate(userPrincipal,team,gameDate, page, size);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/schedule/dayOfGameIsNull/")
    @Operation(summary = "경기가 없는 날짜 조회", description = "년,월을 입력 시 각 팀별 경기가 없는 날을 조회합니다.(전체) 입력 가능")
    public ResponseEntity<BaseBallSchedulePerMonthDTO> getScheduleWithNullOfGame(
            @RequestParam(defaultValue = "전체") String team,
            @RequestParam(defaultValue = "2024-07") YearMonth yearMonth
            ){
        BaseBallSchedulePerMonthDTO dayOfGameIsNull = baseballService.getDayOfGameIsNull(team, yearMonth);
        return ResponseEntity.ok(dayOfGameIsNull);
    }

    @GetMapping("/update")
    @Operation(summary = "경기 크롤링 기능 API 형태로 해놓은 것...사용X")
    public List<Baseball> getSchedule() {
        return baseballService.scrapeAllSchedule();
    }
}