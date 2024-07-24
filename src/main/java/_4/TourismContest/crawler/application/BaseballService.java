package _4.TourismContest.crawler.application;

import _4.TourismContest.crawler.domain.Baseball;
import _4.TourismContest.crawler.repository.BaseballRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BaseballService {
    private final BaseballRepository scheduleRepository;
    private final String os = System.getProperty("os.name").toLowerCase();
    private final BaseballRepository baseballRepository;

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
            for (int i = 1; i <= 12; i++) {
                LocalDate firstDayOfMonth = LocalDate.of(2024, i, 1);
                String formattedDate = firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String url = "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + formattedDate;

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
                        if(month != i){
                            continue;
                        }

                        Elements games = day.select("ul > li");

                        for (Element game : games) {
                            String time = game.select(".MatchBox_time__nIEfd").text().replace("경기 시간", "").trim();
                            String status = game.select(".MatchBox_status__2pbzi").text();

                            // Home, Away team 요소가 로드될 때까지 기다립니다.
                            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MatchBoxTeamArea_team_item__3w5mq")));
                            Element homeEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").last();
                            Element awayEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").first();

                            String homeTeam = homeEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                            String awayTeam = awayEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                            String homeScore = null;
                            String awayScore = null;
                            String homePitcher = null;
                            String awayPitcher = null;
                            String location = game.select(".MatchBox_stadium__13gft").text().replace("경기장", "").trim();
                            Baseball schedule = null;

                            boolean checkTieGame = false;
                            boolean isPitcherNull = true;

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
                                if(awayPitcher != null && homePitcher != null){
                                    isPitcherNull = false;
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
                                        .date(dayOfMonth)
                                        .month(month)
                                        .weekDay(weekday)
                                        .time(time)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .location(location)
                                        .status(status)
                                        .build();
                            } else if(status.equals("종료")){    //경기가 종룓될 경우
                                    schedule = Baseball.builder()
                                            .date(dayOfMonth)
                                            .month(month)
                                            .weekDay(weekday)
                                            .time(time)
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
                                            .date(dayOfMonth)
                                            .month(month)
                                            .weekDay(weekday)
                                            .time(time)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .homePitcher(homePitcher)
                                            .awayPitcher(awayPitcher)
                                            .status(status)
                                            .build();
                                }else{
                                    schedule = Baseball.builder()
                                            .date(dayOfMonth)
                                            .month(month)
                                            .weekDay(weekday)
                                            .time(time)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .status(status)
                                            .build();
                                }
                            }else{  //경기가 진행중일 경우
                                schedule = Baseball.builder()
                                        .date(dayOfMonth)
                                        .month(month)
                                        .weekDay(weekday)
                                        .time(time)
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
                        }
                    }
                }
            }
            scheduleRepository.saveAll(schedules);
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
            String url = "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + formattedDate;

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
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".MatchBoxTeamArea_team__3aB4O")));
                        Thread.sleep(2000);

                        Element homeEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").last();
                        Element awayEle = game.select(".MatchBoxTeamArea_team_item__3w5mq").first();
                        String homeTeam = homeEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                        String awayTeam = awayEle.select(".MatchBoxTeamArea_team__3aB4O").text();
                        String location = game.select(".MatchBox_stadium__13gft").text().replace("경기장", "").trim();

                        Optional<Baseball> findByMonthAndDateAndTimeAndAwayAndHomeAndLocation = baseballRepository.findByMonthAndDateAndTimeAndAwayAndHomeAndLocation(month, dayOfMonth,time,homeTeam, awayTeam, location);
                        if(!findByMonthAndDateAndTimeAndAwayAndHomeAndLocation.isPresent()){
                            //기존 DB에 없는 경기가 추가될 경우
                            //새로운 경기 일정 추가
                            Baseball schedule = null;
                            String homeScore = null;
                            String awayScore = null;
                            String homePitcher = null;
                            String awayPitcher = null;

                            boolean checkTieGame = false;
                            boolean isPitcherNull = true;
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
                                if(awayPitcher != null && homePitcher != null){
                                    isPitcherNull = false;
                                }
                                isPitcherNull = true;
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
                                        .date(dayOfMonth)
                                        .month(month)
                                        .weekDay(weekday)
                                        .time(time)
                                        .home(homeTeam)
                                        .away(awayTeam)
                                        .location(location)
                                        .status(status)
                                        .build();
                            } else if(status.equals("종료")){    //경기가 종룓될 경우
                                schedule = Baseball.builder()
                                        .date(dayOfMonth)
                                        .month(month)
                                        .weekDay(weekday)
                                        .time(time)
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
                                            .date(dayOfMonth)
                                            .month(month)
                                            .weekDay(weekday)
                                            .time(time)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .status(status)
                                            .homePitcher(homePitcher)
                                            .awayPitcher(awayPitcher)
                                            .build();
                                }else{
                                    schedule = Baseball.builder()
                                            .date(dayOfMonth)
                                            .month(month)
                                            .weekDay(weekday)
                                            .time(time)
                                            .home(homeTeam)
                                            .away(awayTeam)
                                            .location(location)
                                            .status(status)
                                            .build();
                                }
                            }else{  //경기가 진행중일 경우
                                schedule = Baseball.builder()
                                        .date(dayOfMonth)
                                        .month(month)
                                        .weekDay(weekday)
                                        .time(time)
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
                            Baseball baseball = findByMonthAndDateAndTimeAndAwayAndHomeAndLocation.get();
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

                            // Update baseball object
                            if(awayScore!= null || homeScore != null){
                                baseball.setAwayScore(Integer.parseInt(awayScore));
                                baseball.setHomeScore(Integer.parseInt(homeScore));
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
}