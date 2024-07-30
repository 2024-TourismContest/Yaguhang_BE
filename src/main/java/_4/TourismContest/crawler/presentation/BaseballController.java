package _4.TourismContest.crawler.presentation;

import _4.TourismContest.crawler.application.BaseballService;
import _4.TourismContest.crawler.domain.Baseball;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class BaseballController {

    private final BaseballService crawlingService;

    @GetMapping("/update")
    public List<Baseball> getSchedule() {
        return crawlingService.scrapeAllSchedule();
    }
    // 특정 날짜 조회
    @GetMapping("/update2")
    public void getSchedule2() {
        crawlingService.scrapeTodayGame();
    }

}