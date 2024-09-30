package _4.TourismContest.email.scheduler;

import _4.TourismContest.baseball.domain.BaseballScrap;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.email.application.EmailService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class EmailScheduler {
    private final EmailService emailService;
    private final BaseballScrapRepository baseballScrapRepository;

    @Scheduled(cron = "0 0 12 * * *")
    @Transactional
    public void sendEmail3DaysAgo() { // 하루 전 메일 알림, 12시에 전송
        LocalDateTime startOfThreeDaysLater = LocalDateTime.now().plusDays(1).with(LocalTime.MIN);
        LocalDateTime endOfThreeDaysLater = LocalDateTime.now().plusDays(1).with(LocalTime.MAX);
        List<BaseballScrap> baseballScraps = baseballScrapRepository.findBaseballByDate(startOfThreeDaysLater, endOfThreeDaysLater);
        for(BaseballScrap baseballScrap: baseballScraps){
            String toSend = baseballScrap.getUser().getEmail();
            String title = makeTitle(baseballScrap, "내일");
            String message = makeMessage(baseballScrap, "**내일**");
            emailService.sendEmail(toSend, title, message);
        }
    }

    @Scheduled(cron = "0 0 10 * * *")
    @Transactional
    public void sendEmailToday() {  // 당일 경기 알림, 10시에 전송
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN); // 오늘의 시작
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);   // 오늘의 끝
        List<BaseballScrap> baseballScraps = baseballScrapRepository.findBaseballByDate(startOfDay, endOfDay);
        for(BaseballScrap baseballScrap: baseballScraps){
            String toSend = baseballScrap.getUser().getEmail();
            String title = makeTitle(baseballScrap, "오늘");
            String message = makeMessage(baseballScrap, "**오늘**");
            emailService.sendEmail(toSend, title, message);
            System.out.println(baseballScrap.getUser().getEmail() + " 로 전송 완료");
        }
    }

    private String makeMessage(BaseballScrap baseballScrap, String restDay){
        String userName = baseballScrap.getUser().getNickname();
        return "안녕하세요, " + userName + "님!\n" +
                "\n" +
                "관심 있어하시던 **"+baseballScrap.getBaseball().getHome()+" vs "+baseballScrap.getBaseball().getAway()+
                "** 경기가 " + restDay + " 진행됩니다. 잊지 않고 경기를 즐기실 수 있도록 미리 알려드립니다!\n" +
                "\n" +
                "경기 정보:\n" +
                "- **경기 일시**: "+baseballScrap.getBaseball().getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))+ " " +
                baseballScrap.getBaseball().getWeekDay() +"\n" +
                "- **경기 장소**: "+baseballScrap.getBaseball().getLocation()+" 야구장\n" +
                "\n" +
                "미리 준비하셔서 멋진 경기 관람 되시길 바랍니다. 즐거운 하루 보내세요!\n" +
                "\n" +
                "감사합니다.\n" +
                "-야구행-";
    }

    private String makeTitle(BaseballScrap baseballScrap, String restDay){ // restDay : 3일 뒤에, 오늘
        String userName = baseballScrap.getUser().getNickname();
        return "📅 [경기 알림] " + userName + "님! " + restDay + " 예정된 야구 경기를 잊지 마세요!";
    }
}
