package _4.TourismContest.crawler.scheduler;

import _4.TourismContest.crawler.application.BaseballService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BaseballScheduler {

    @Autowired
    private BaseballService baseballService;

    @PostConstruct
    public void init() {
        baseballService.scrapeAllSchedule();
        System.out.println("BaseballScheduler.init");
    }

    @Scheduled(cron = "0 */30 * * * *") // 매 30분마다 실행
    public void scrapeGames() {
        System.out.println("BaseballScheduler.scrapeGames");
        baseballService.scrapeTodayGame();
    }
}
