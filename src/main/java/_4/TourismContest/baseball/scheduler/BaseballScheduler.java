package _4.TourismContest.baseball.scheduler;

import _4.TourismContest.baseball.application.BaseballService;
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
    }

    @Scheduled(cron = "0 0,30 0,13-23 * * *") // 매 30분마다 실행하되 01시~12시 제외
    public void scrapeGames() {
        System.out.println("BaseballScheduler.scrapeGames");
        baseballService.scrapeTodayGame();
    }
}
