package _4.TourismContest.baseball.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.dto.BaseBallDTO;
import _4.TourismContest.baseball.dto.BaseBallSchedulePerMonthDTO;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import _4.TourismContest.weather.application.WeatherForecastService;
import _4.TourismContest.weather.domain.WeatherForecast;
import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import _4.TourismContest.weather.repository.WeatherForecastRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseballService {
    private final String os = System.getProperty("os.name").toLowerCase();
    private final BaseballRepository baseballRepository;
    private final BaseballScrapService baseballScrapService;
    private final WeatherForecastService weatherForecastService;

    private final Map<String, String> teamLogoMap = Map.of(
            "두산 베어스", "Doosan.png",
            "LG 트윈스", "LGTwins.png",
            "KT 위즈", "KtWizs.png",
            "SSG 랜더스", "SSGLanders.png",
            "NC 다이노스", "NCDinos.png",
            "KIA 타이거즈", "KIA.png",
            "롯데 자이언츠", "Lotte.png",
            "삼성 라이온즈", "Samsung.png",
            "한화 이글스", "Hanwha.png",
            "키움 히어로즈", "Kiwoom.png"
    );

    @Transactional
    public List<Baseball> scrapeAllSchedule() {
        setUpWebDriver();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ArrayList<Baseball> schedules = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 12; i++) {
                LocalDate firstDayOfMonth = LocalDate.of(today.getYear(), i, 1);
                String formattedDate = firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String url = "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + formattedDate + "&postSeason=Y";

                driver.get(url);
                Thread.sleep(2000); // 페이지 로드를 위한 대기 시간

                Document doc = Jsoup.parse(driver.getPageSource());
                Elements days = doc.select(".ScheduleLeagueType_match_list_container__1v4b0 > div");

                for (Element day : days) {
                    // 날짜 가져오기
                    Element dateElement = day.selectFirst(".ScheduleLeagueType_group_title__S2Z_g .ScheduleLeagueType_title_area__3v4qt .ScheduleLeagueType_title__2Kalm");
                    if (dateElement != null) {
                        String dateText = dateElement.text();
                        String[] parts = dateText.split(" ");
                        int month = Integer.parseInt(parts[0].replace("월", ""));
                        int dayOfMonth = Integer.parseInt(parts[1].replace("일", ""));
                        String weekday = parts[2].replace("(", "").replace(")", "");
                        if (month != i) {
                            continue;
                        }

                        Elements games = day.select("ul > li");

                        for (Element game : games) {
                            try {
                                String time = game.select(".MatchBox_time__nIEfd").text().replace("경기 시간", "").trim();
                                String status = game.select(".MatchBox_status__2pbzi").text();

                                String[] timeParts = time.split(":");
                                int hour = Integer.parseInt(timeParts[0]);
                                int minute = Integer.parseInt(timeParts[1]);
                                LocalDateTime gameTime = LocalDateTime.of(firstDayOfMonth.getYear(), month, dayOfMonth, hour, minute);
                                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MatchBoxTeamArea_team_item__3w5mq")));
                                Element homeEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").last();
                                Element awayEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").first();

                                String homeTeam = homeEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                                String awayTeam = awayEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                                String location = game.select(".MatchBox_stadium__13gft").text().replace("경기장", "").trim();

                                // 중복된 경기가 있는지 확인
                                if (baseballRepository.findByTimeAndHomeAndAwayAndLocation(gameTime, homeTeam, awayTeam, location).isPresent()) {
                                    System.out.println("이미 등록된 경기: " + homeTeam + " vs " + awayTeam + " at " + gameTime);
                                    continue; // 중복된 경기가 있으면 continue로 다음 게임으로 넘어감
                                }

                                String homeScore = null;
                                String awayScore = null;
                                String homePitcher = null;
                                String awayPitcher = null;
                                Baseball schedule = null;

                                boolean checkTieGame = false;
                                boolean isPitcherNull = false;

                                // 경기 상태에 따라 다른 처리
                                if (status.equals("종료")) {
                                    // 종료된 경기의 점수 및 승패 처리
                                    boolean isHomeTeamWinner = false;
                                    if (homeEle.className().contains("winner")) {
                                        isHomeTeamWinner = true;
                                    } else if (homeEle.className().contains("loser")) {
                                        isHomeTeamWinner = false;
                                    } else {
                                        checkTieGame = true;
                                    }
                                    // 무승부 처리
                                    if (checkTieGame && !isHomeTeamWinner) {
                                        Elements scoreDivs = game.select(".MatchBoxTeamArea_score_wrap__3eSae");
                                        if (!scoreDivs.isEmpty()) {
                                            List<Element> scoreElements = scoreDivs.select(".MatchBoxTeamArea_score__1_YFB");
                                            homeScore = scoreElements.get(0).text();
                                        }
                                        awayScore = homeScore;
                                        awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                                .select(".MatchBoxTeamArea_item__11GUB").text();
                                        homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                                .select(".MatchBoxTeamArea_item__11GUB").text();
                                    } else {
                                        // 승패가 있는 경우 처리
                                        if (isHomeTeamWinner) {
                                            homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                    .select(".MatchBoxTeamArea_score__1_YFB")
                                                    .text();
                                            awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                    .select(".MatchBoxTeamArea_score__1_YFB")
                                                    .text();
                                            homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                    .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                    .last()
                                                    .text();
                                            awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                    .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                    .last()
                                                    .text();
                                        } else {
                                            awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                    .select(".MatchBoxTeamArea_score__1_YFB")
                                                    .text();
                                            homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                    .select(".MatchBoxTeamArea_score__1_YFB")
                                                    .text();
                                            awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                    .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                    .last()
                                                    .text();
                                            homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                    .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                    .last()
                                                    .text();
                                        }
                                    }
                                } else if (status.equals("예정")) {
                                    // 예정된 경기 처리
                                    awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                    homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                    if (awayPitcher.equals("") && homePitcher.equals("")) {
                                        isPitcherNull = true;
                                    }
                                }

                                Integer homeScoreValue = (homeScore != null && !homeScore.isEmpty()) ? Integer.parseInt(homeScore) : 0;
                                Integer awayScoreValue = (awayScore != null && !awayScore.isEmpty()) ? Integer.parseInt(awayScore) : 0;

                                // 경기 객체 생성 및 저장
                                schedule = Baseball.builder()
                                        .time(gameTime)
                                        .weekDay(weekday)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .location(location)
                                        .status(status)
                                        .homeScore(homeScoreValue)
                                        .awayScore(awayScoreValue)
                                        .homePitcher(homePitcher)
                                        .awayPitcher(awayPitcher)
                                        .build();

                                // Save the schedule
                                baseballRepository.save(schedule);
                            } catch (Exception gameException) {
                                System.err.println("Failed to process game element: " + game);
                                gameException.printStackTrace();
                                // Continue with the next game
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return schedules;
    }

    @Transactional
    public void scrapeTodayGame() {
        setUpWebDriver();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        ArrayList<Baseball> schedules = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String url = "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + formattedDate + "&postSeason=Y";

            driver.get(url);
            Thread.sleep(2000);

            Document doc = Jsoup.parse(driver.getPageSource());
            Elements days = doc.select(".ScheduleLeagueType_match_list_container__1v4b0 > div");

            for (Element day : days) {
                // 날짜 가져오기
                Element dateElement = day.selectFirst(".ScheduleLeagueType_group_title__S2Z_g .ScheduleLeagueType_title_area__3v4qt .ScheduleLeagueType_title__2Kalm");
                if (dateElement != null) {
                    String dateText = dateElement.text();
                    String[] parts = dateText.split(" ");
                    int month = Integer.parseInt(parts[0].replace("월", ""));
                    int dayOfMonth = Integer.parseInt(parts[1].replace("일", ""));
                    String weekday = parts[2].replace("(", "").replace(")", "");

                    if (dayOfMonth != today.getDayOfMonth()) {
                        continue;
                    }

                    Elements games = day.select("ul > li");
                    for (Element game : games) {
                        String time = game.select(".MatchBox_time__nIEfd").text().replace("경기 시간", "").trim();
                        String status = game.select(".MatchBox_status__2pbzi").text();

                        String[] timeParts = time.split(":");
                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);
                        LocalDateTime gameTime = LocalDateTime.of(today.getYear(),month,dayOfMonth,hour,minute);

                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MatchBoxTeamArea_team__3aB4O")));
                        Thread.sleep(2000);

                        Element homeEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").last();
                        Element awayEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").first();
                        String homeTeam = homeEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                        String awayTeam = awayEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                        String location = game.select(".MatchBox_stadium__13gft").text().replace("경기장", "").trim();

                        Optional<Baseball> findByTimeAndHomeAndAwayAndLocation = baseballRepository.findByTimeAndHomeAndAwayAndLocation(gameTime,homeTeam, awayTeam, location);
                        if(!findByTimeAndHomeAndAwayAndLocation.isPresent()){
                            //기존 DB에 없는 경기가 추가될 경우
                            //새로운 경기 일정 추가
                            Baseball schedule = null;
                            String homeScore = null;
                            String awayScore = null;
                            String homePitcher = null;
                            String awayPitcher = null;

                            boolean checkTieGame = false;
                            boolean isPitcherNull = false;
                            if (status.equals("종료")) {
                                boolean isHomeTeamWinner= false;    //홈팀이 이겼는지, 졌는지, 무승부인지
                                if(homeEle.className().contains("winner")){
                                    isHomeTeamWinner = true;
                                }else if(homeEle.className().contains("loser")){
                                    isHomeTeamWinner = false;
                                }else{
                                    checkTieGame = true;
                                }
                                if(checkTieGame && !isHomeTeamWinner){   //무승부일 경우
                                    Elements scoreDivs = game.select(".MatchBoxTeamArea_score_wrap__3eSae");
                                    if (!scoreDivs.isEmpty()) {
                                        List<Element> scoreElements = scoreDivs.select(".MatchBoxTeamArea_score__1_YFB");
                                        homeScore = scoreElements.get(0).text();
                                    }
                                    awayScore = homeScore;
                                    awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                    homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                }else{  //승,패가 정해진 경우
                                    if(isHomeTeamWinner){   //홈팀이 이겼을 경우
                                        homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_score__1_YFB")
                                                .text();
                                        awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_score__1_YFB")
                                                .text();
                                        homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last()
                                                .text();
                                        awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last()
                                                .text();
                                    }else { //홈팀이 졌을 경우
                                        awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_score__1_YFB")
                                                .text();
                                        homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_score__1_YFB")
                                                .text();
                                        awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last()
                                                .text();
                                        homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last()
                                                .text();
                                    }
                                }
                            }else if(status.equals("취소")){

                            }else if(status.equals("예정")){
                                awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                if(awayPitcher.equals("") && homePitcher.equals("")){
                                    isPitcherNull = true;
                                }
                            }else{  //경기가 진행중일 경우
                                awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                Elements scoreDivs = game.select(".MatchBoxTeamArea_score_wrap__3eSae");
                                awayScore = scoreDivs.first().text();
                                homeScore = scoreDivs.last().text();
                                awayScore = awayScore.replaceAll("[^0-9]", "");
                                homeScore = homeScore.replaceAll("[^0-9]", "");
                            }
                            if (status.equals("취소")) {    //경기가 취소될 경우
                                schedule = Baseball.builder()
                                        .weekDay(weekday)
                                        .time(gameTime)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .location(location)
                                        .status(status)
                                        .build();
                            } else if(status.equals("종료")){    //경기가 종룓될 경우
                                schedule = Baseball.builder()
                                        .weekDay(weekday)
                                        .time(gameTime)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .homeScore(Integer.parseInt(homeScore))
                                        .awayScore(Integer.parseInt(awayScore))
                                        .homePitcher(homePitcher)
                                        .awayPitcher(awayPitcher)
                                        .location(location)
                                        .status(status)
                                        .build();
                            }else if(status.equals("예정")){
                                if(!isPitcherNull){
                                    schedule = Baseball.builder()
                                            .weekDay(weekday)
                                            .time(gameTime)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .status(status)
                                            .homePitcher(homePitcher)
                                            .awayPitcher(awayPitcher)
                                            .build();
                                }else{
                                    schedule = Baseball.builder()
                                            .weekDay(weekday)
                                            .time(gameTime)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .status(status)
                                            .build();
                                }
                            }else{  //경기가 진행중일 경우
                                schedule = Baseball.builder()
                                        .weekDay(weekday)
                                        .time(gameTime)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .homeScore(Integer.parseInt(homeScore))
                                        .awayScore(Integer.parseInt(awayScore))
                                        .homePitcher(homePitcher)
                                        .awayPitcher(awayPitcher)
                                        .location(location)
                                        .status(status)
                                        .build();
                            }
                            schedules.add(schedule);
                        }else{
                            //기존에 DB에 데이터가 있는 경우
                            Baseball baseball = findByTimeAndHomeAndAwayAndLocation.get();
                            String homeScore = null;
                            String awayScore = null;
                            String homePitcher = null;
                            String awayPitcher = null;

                            boolean checkTieGame = false;
                            if (status.equals("종료")) {
                                boolean isHomeTeamWinner = false;
                                if (homeEle.className().contains("winner")) {
                                    isHomeTeamWinner = true;
                                } else if (homeEle.className().contains("loser")) {
                                    isHomeTeamWinner = false;
                                } else {
                                    checkTieGame = true;
                                }

                                if (checkTieGame) { // Tie game
                                    Elements scoreDivs = game.select(".MatchBoxTeamArea_score_wrap__3eSae");
                                    if (!scoreDivs.isEmpty()) {
                                        List<Element> scoreElements = scoreDivs.select(".MatchBoxTeamArea_score__1_YFB");
                                        homeScore = scoreElements.get(0).text();
                                    }
                                    awayScore = homeScore;
                                    awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                    homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                            .select(".MatchBoxTeamArea_item__11GUB").text();
                                } else { // Winner and loser identified
                                    if (isHomeTeamWinner) { // Home team won
                                        homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_score__1_YFB").text();
                                        awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_score__1_YFB").text();
                                        homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last().text();
                                        awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last().text();
                                    } else { // Home team lost
                                        awayScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_score__1_YFB").text();
                                        homeScore = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_score__1_YFB").text();
                                        awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_winner__2o1Hm")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last().text();
                                        homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq.MatchBoxTeamArea_type_loser__2ym2q")
                                                .select(".MatchBoxTeamArea_sub_info__3O3LO .MatchBoxTeamArea_item__11GUB")
                                                .last().text();
                                    }
                                }
                            } else if (status.equals("취소")) {
                                // Handle canceled games if needed
                            } else if (status.equals("예정")) {
                                // Handle scheduled games if needed
                            } else { // Game in progress
                                awayPitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").first()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                homePitcher = game.select(".MatchBoxTeamArea_team_item__3w5mq").last()
                                        .select(".MatchBoxTeamArea_item__11GUB").text();
                                Elements scoreDivs = game.select(".MatchBoxTeamArea_score_wrap__3eSae");
                                awayScore = scoreDivs.first().text();
                                homeScore = scoreDivs.last().text();
                                awayScore = awayScore.replaceAll("[^0-9]", "");
                                homeScore = homeScore.replaceAll("[^0-9]", "");
                            }

                            Integer homeScoreValue = (homeScore != null && !homeScore.isEmpty()) ? Integer.parseInt(homeScore) : 0;
                            Integer awayScoreValue = (awayScore != null && !awayScore.isEmpty()) ? Integer.parseInt(awayScore) : 0;
                            // Update baseball object
                            if(awayScore!= null || homeScore != null){
                                baseball.setAwayScore(awayScoreValue);
                                baseball.setHomeScore(homeScoreValue);
                            }
                            baseball.setAwayPitcher(awayPitcher);
                            baseball.setHomePitcher(homePitcher);
                            baseball.setStatus(status);
                            baseballRepository.save(baseball);
                            schedules.add(baseball);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }



    //크롬 드라이버 셋업
    private void setUpWebDriver() {
        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "drivers/chromedriver_win.exe");
        } else if (os.contains("mac")) {
            System.setProperty("webdriver.chrome.driver", "/Users/minseok/chromedriver-mac-arm64/chromedriver");
        } else if (os.contains("linux")) {
            System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        }
    }

    /**
     * 각 팀의 경기 일정 가져오기
     * @param team (팀명 or 전체)
     * @param page (원하는 날짜 인덱스, 0 부터 시작...)
     * @param size (데이터 요청 크기)
     */
    public BaseballScheduleDTO getGamesByTeamAndDate(UserPrincipal userPrincipal, String team, LocalDate gameDate, int page, int size) {
        LocalDate today = (gameDate != null) ? gameDate : LocalDate.now();
//        LocalDate today = (gameDate != null) ? gameDate : LocalDate.of(2024, 9, 20);
        LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIDNIGHT);

        Page<Baseball> baseballPage;
        if ("전체".equals(team)) {
            baseballPage = baseballRepository.findByStatusNotAndTimeIsAfterOrderByTime("취소",startOfDay, PageRequest.of(page, size));
        } else {
            baseballPage = baseballRepository.findByStatusNotAndTimeIsAfterAndHomeOrAwayOrderByTime("취소", startOfDay, team, PageRequest.of(page, size));
        }

        List<BaseBallDTO> baseballSchedules = baseballPage.getContent().stream()
                .map(baseball -> {
                    String homeTeam = exchangeTeamName(baseball.getHome());
                    String homeTeamOut = homeTeam.replace(" ", "\n");
                    String awayTeam = exchangeTeamName(baseball.getAway());
                    String awayTeamOut = awayTeam.replace(" ", "\n");
                    WeatherForecastEnum weatherForecast = weatherForecastService.getWeatherForecastDataWithGame(baseball);

                    return BaseBallDTO.builder()
                            .id(baseball.getId())
                            .home(homeTeamOut)
                            .away(awayTeamOut)
                            .homeTeamLogo(getTeamLogoUrl(homeTeam))
                            .awayTeamLogo(getTeamLogoUrl(awayTeam))
                            .stadium(baseball.getLocation())
                            .date(formatLocalDateTime(baseball.getTime()))
                            .time(baseball.getTime().toLocalTime().toString())
                            .weather(weatherForecast)
                            .weatherUrl(getWeatherUrl(weatherForecast))
                            .isScraped(baseballScrapService.getIsScrapped(userPrincipal, baseball.getId()))
                            .build();
                })
                .collect(Collectors.toList());

        return BaseballScheduleDTO.builder()
                .team(team)
                .pageIndex(page)
                .pageSize(size)
                .date(formatLocalDateTime(startOfDay))
                .schedules(baseballSchedules)
                .build();
    }

    private String getWeatherUrl(WeatherForecastEnum weatherForecastDataWithGame) {
        String baseUrl = "https://yaguhang.kro.kr:8443/weatherImages/";
        if(weatherForecastDataWithGame == null){
            return baseUrl + "null.svg";
        }
        switch (weatherForecastDataWithGame){
            case CLOUDY -> {
                return baseUrl + "Cloudy.svg";
            }
            case OVERCAST -> {
                return baseUrl + "Overcast.svg";
            }
            case RAINY -> {
                return baseUrl + "Rain.svg";
            }
            case SHOWER -> {
                return baseUrl + "Shower.svg";
            }
            case SNOW -> {
                return baseUrl + "Snow.svg";
            }
            case SUNNY -> {
                return baseUrl + "Sunny.svg";
            }
            default -> {
                throw new IllegalArgumentException("Check Weather Status");
            }
        }
    }

    /**
     * 1달 간 경기가 없는 날짜 반환
     * @param gameTime
     * @return
     */
    public BaseBallSchedulePerMonthDTO getDayOfGameIsNull(String team, YearMonth gameTime){
        LocalDate startOfMonth = gameTime.atDay(1);  // 월 초
        LocalDate endOfMonth = gameTime.atEndOfMonth();  // 월 말

        List<LocalDate> dayOfGameIsNull = new ArrayList<>();

        if(team.equals("전체")){
            // 월의 각 날짜를 반복하면서 경기가 없는 날짜를 찾음
            for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
                // 특정 날짜에 경기가 있는지 확인
                boolean isGameOnDate = baseballRepository.existsByTimeBetween(date.atStartOfDay(), date.atTime(23, 59, 59));

                // 경기가 없는 날짜를 리스트에 추가
                if (!isGameOnDate) {
                    dayOfGameIsNull.add(date);
                }
            }
        }else {
            // 특정 팀의 경기 일정이 없는 날짜를 찾음
            for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
                // 특정 날짜에 해당 팀이 홈팀 또는 원정팀으로 경기 중인지 확인
                boolean isTeamPlayingAsHome = baseballRepository.existsByHomeAndTimeBetween(team, date.atStartOfDay(), date.atTime(23, 59, 59));
                boolean isTeamPlayingAsAway = baseballRepository.existsByAwayAndTimeBetween(team, date.atStartOfDay(), date.atTime(23, 59, 59));

                // 해당 팀의 경기가 없는 날짜를 리스트에 추가
                if (!isTeamPlayingAsHome && !isTeamPlayingAsAway) {
                    dayOfGameIsNull.add(date);
                }
            }
        }
        return BaseBallSchedulePerMonthDTO.builder()
                .team(team)
                .dayOfGameIsNull(dayOfGameIsNull)
                .build();
    }
    private String getTeamLogoUrl(String team) {
        String baseUrl = "https://yaguhang.kro.kr:8443/teamLogos/";
        String logoFileName = teamLogoMap.get(team);

        if (logoFileName == null) {
            throw new IllegalArgumentException("Unknown team: " + team + ". Please check the team name.");
        }

        return baseUrl + logoFileName;
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN);
        String formattedDate = dateTime.format(dateFormatter);

        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String dayOfWeekKorean = getKoreanDayOfWeek(dayOfWeek);

        return formattedDate + "" +dayOfWeekKorean;
    }

    private String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "(월))";
            case TUESDAY: return "(화)";
            case WEDNESDAY: return "(수)";
            case THURSDAY: return "(목)";
            case FRIDAY: return "(금)";
            case SATURDAY: return "(토)";
            case SUNDAY: return "(일)";
            default: throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }
    private String exchangeTeamName(String teamName){
        switch (teamName){
            case "LG" : return "LG 트윈스";
            case "KT" : return "KT 위즈";
            case "SSG" : return "SSG 랜더스";
            case "NC" : return "NC 다이노스";
            case "두산" : return "두산 베어스";
            case "KIA" : return "KIA 타이거즈";
            case "롯데" : return "롯데 자이언츠";
            case "삼성" : return "삼성 라이온즈";
            case "한화" : return "한화 이글스";
            case "키움" : return "키움 히어로즈";
            default: throw new IllegalArgumentException("Invalid team name: "+ teamName);
        }
    }
}