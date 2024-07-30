package _4.TourismContest.crawler.presentation;

import _4.TourismContest.crawler.application.BaseballService;
import _4.TourismContest.crawler.domain.Baseball;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
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
    public ResponseEntity<PagedModel<Baseball>> getGames(
            @RequestParam String team,
            @RequestParam int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler assembler
            ) {
        // JWT 토큰 검증 (토큰 유효성 검증 코드 생략)
        Page<Baseball> games = baseballService.getGamesByTeamAndDate(team, page, size);
        return ResponseEntity.ok(assembler.toModel(games));
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