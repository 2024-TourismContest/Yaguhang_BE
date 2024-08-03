package _4.TourismContest.baseball.scheduler;

import _4.TourismContest.baseball.application.BaseballService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

@Component
public class BaseballScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BaseballScheduler.class);

    @Autowired
    private BaseballService baseballService;

//    @PostConstruct
//    public void init() {
//        try {
//            baseballService.scrapeAllSchedule();
//        } catch (DataIntegrityViolationException e) {
//            // Handle unique constraint violations and other data integrity issues
//            logger.error("Data integrity violation occurred during PostConstruct initialization", e);
//        } catch (Exception e) {
//            // Handle any other unexpected exceptions
//            logger.error("An unexpected error occurred during PostConstruct initialization", e);
//        }
//    }

    @Scheduled(cron = "0 0,30 0,13-23 * * *") // Every 30 minutes excluding 01:00-12:00
    public void scrapeGames() {
        System.out.println("BaseballScheduler.scrapeGames");
        baseballService.scrapeTodayGame();
    }
}