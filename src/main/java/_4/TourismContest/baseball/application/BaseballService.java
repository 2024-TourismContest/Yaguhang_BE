package _4.TourismContest.baseball.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.dto.BaseBallDTO;
import _4.TourismContest.baseball.dto.BaseBallSchedulePerMonthDTO;
import _4.TourismContest.baseball.dto.BaseballScheduleDTO;
import _4.TourismContest.baseball.dto.ScheduleDateInfo;
import _4.TourismContest.baseball.dto.ScheduleMeta;
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
    private static final long PAGE_LOAD_WAIT_MS = 2000;

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
        WebDriver driver = createWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<Baseball> schedules = new ArrayList<>();
        try {
            for (int month = 3; month <= 11; month++) {
                Document doc = fetchMonthlyDocument(driver, month);
                schedules.addAll(parseMonthlySchedules(doc, month, wait));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while fetching schedules", e);
        } finally {
            driver.quit();
        }
        return schedules;
    }

    @Transactional
    public List<Baseball> scrapeTodayGame() {
        WebDriver driver = createWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<Baseball> todaySchedules = new ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            Document doc = fetchMonthlyDocument(driver, today.getMonthValue());
            Elements days = doc.select(".ScheduleLeagueType_match_list_container__1v4b0 > div");
            for (Element day : days) {
                Element dateEl = day.selectFirst(
                        ".ScheduleLeagueType_group_title__S2Z_g .ScheduleLeagueType_title_area__3v4qt .ScheduleLeagueType_title__2Kalm"
                );
                if (dateEl == null) continue;
                ScheduleDateInfo dateInfo = parseDateInfo(dateEl.text());
                if (dateInfo.getMonth() != today.getMonthValue() || dateInfo.getDay() != today.getDayOfMonth()) {
                    continue;
                }
                // 오늘 날짜 컨테이너 처리
                for (Element gameEl : day.select("ul > li")) {
                    Optional<Baseball> saved = updateOrInsertGame(gameEl, dateInfo, wait);
                    saved.ifPresent(todaySchedules::add);
                }
                break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while scraping today games", e);
        } finally {
            driver.quit();
        }
        return todaySchedules;
    }

    private Document fetchMonthlyDocument(WebDriver driver, int month) throws InterruptedException {
        LocalDate firstOfMonth = LocalDate.of(LocalDate.now().getYear(), month, 1);
        String dateParam = firstOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String url = String.format(
                "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=%s&postSeason=Y",
                dateParam);
        driver.get(url);
        Thread.sleep(PAGE_LOAD_WAIT_MS);
        return Jsoup.parse(Objects.requireNonNull(driver.getPageSource()));
    }

    private List<Baseball> parseMonthlySchedules(Document doc, int targetMonth, WebDriverWait wait) {
        List<Baseball> list = new ArrayList<>();
        Elements days = doc.select(".ScheduleLeagueType_match_list_container__1v4b0 > div");
        for (Element day : days) {
            Element dateEl = day.selectFirst(
                    ".ScheduleLeagueType_group_title__S2Z_g .ScheduleLeagueType_title__2Kalm");
            if (dateEl == null) continue;

            ScheduleDateInfo dateInfo = parseDateInfo(dateEl.text());
            if (dateInfo.getMonth() != targetMonth) continue;

            for (Element gameEl : day.select("ul > li")) {
                list.addAll(processGameElement(gameEl, dateInfo, wait));
            }
        }
        return list;
    }

    private List<Baseball> processGameElement(Element gameEl, ScheduleDateInfo dateInfo, WebDriverWait wait) {
        List<Baseball> results = new ArrayList<>();
        try {
            ScheduleMeta meta = extractMeta(gameEl, dateInfo, wait);
            Optional<Baseball> existing = baseballRepository
                    .findByTimeAndHomeAndAwayAndLocation(
                            meta.getGameTime(), meta.getHomeTeam(), meta.getAwayTeam(), meta.getLocation());
            if (existing.isPresent()) return results;

            System.out.println("meta = " + meta);
            Baseball schedule = Baseball.builder()
                    .time(meta.getGameTime())
                    .weekDay(dateInfo.getWeekday())
                    .home(meta.getHomeTeam())
                    .away(meta.getAwayTeam())
                    .location(meta.getLocation())
                    .status(meta.getStatus())
                    .homeScore(meta.getHomeScore())
                    .awayScore(meta.getAwayScore())
                    .homePitcher(meta.getHomePitcher())
                    .awayPitcher(meta.getAwayPitcher())
                    .build();

            baseballRepository.save(schedule);
            results.add(schedule);
        } catch (Exception ex) {
            System.err.println("Failed to process game: " + ex.getMessage());
        }
        return results;
    }

    private ScheduleMeta extractMeta(Element game, ScheduleDateInfo dateInfo, WebDriverWait wait) {
        Element timeEl = game.selectFirst(".MatchBox_time__nIEfd");
        if (timeEl == null) throw new IllegalStateException("Time element not found");
        String timeText = timeEl.text().replace("경기 시간", "").trim();

        Element statusEl = game.selectFirst(".MatchBox_status__2pbzi");
        String status = statusEl != null ? statusEl.text() : "";

        String[] hm = timeText.split(":");
        LocalDateTime gameTime = LocalDateTime.of(
                LocalDate.now().getYear(), dateInfo.getMonth(), dateInfo.getDay(),
                Integer.parseInt(hm[0]), Integer.parseInt(hm[1]));

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".MatchBoxHeadToHeadArea_team_item__25jg6")
        ));

        //팀 정보
        Elements teamEls = game.select(".MatchBoxHeadToHeadArea_team_item__25jg6");
        if (teamEls.size() < 2) return null;
        Element awayEl = teamEls.first();
        Element homeEl = teamEls.last();

        String awayTeam = Optional.ofNullable(
                awayEl.selectFirst(".MatchBoxHeadToHeadArea_team__40JQL")
        ).map(Element::text).orElse("");
        String homeTeam = Optional.ofNullable(
                homeEl.selectFirst(".MatchBoxHeadToHeadArea_team__40JQL")
        ).map(Element::text).orElse("");

        // 점수
        int awayScore = parseScore(awayEl, ".MatchBoxHeadToHeadArea_score__e2D7k");
        int homeScore = parseScore(homeEl, ".MatchBoxHeadToHeadArea_score__e2D7k");

        // 투수
        String awayPitcher = parsePitcher(awayEl, ".MatchBoxHeadToHeadArea_item__1IPbQ:last-child");
        String homePitcher = parsePitcher(homeEl, ".MatchBoxHeadToHeadArea_item__1IPbQ:last-child");

        // 위치
        String location = Optional.ofNullable(
                        game.selectFirst(".MatchBox_stadium__13gft")
                ).map(Element::text)
                .orElse("")
                .replace("경기장", "").replace("(신)", "").trim();

        return new ScheduleMeta(
                gameTime, homeTeam, awayTeam,
                location, status,
                homeScore, awayScore,
                homePitcher, awayPitcher
        );
    }

    private ScheduleDateInfo parseDateInfo(String text) {
        // "5월 12일 (화)" 형식 처리
        String[] parts = text.split(" ");
        int month = Integer.parseInt(parts[0].replace("월", ""));
        int day = Integer.parseInt(parts[1].replace("일", ""));
        String weekday = parts[2].replace("(", "").replace(")", "");
        return new ScheduleDateInfo(month, day, weekday);
    }

    private int parseScore(Element teamEl, String selector) {
        return Optional.ofNullable(teamEl.selectFirst(selector))
                .map(Element::text)
                .map(t -> t.replaceAll("\\D", ""))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .orElse(0);
    }

    private String parsePitcher(Element teamEl, String selector) {
        return Optional.ofNullable(teamEl.selectFirst(selector))
                .map(Element::text)
                .orElse("");
    }

    /**
     * 신규 경기면 저장, 기존이면 업데이트 후 반환
     */
    private Optional<Baseball> updateOrInsertGame(Element gameEl, ScheduleDateInfo dateInfo, WebDriverWait wait) {
        ScheduleMeta meta = extractMeta(gameEl, dateInfo, wait);
        if (meta == null) return Optional.empty();
        Optional<Baseball> existingOpt = baseballRepository
                .findByTimeAndHomeAndAwayAndLocation(
                        meta.getGameTime(), meta.getHomeTeam(), meta.getAwayTeam(), meta.getLocation()
                );
        Baseball entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            entity.setStatus(meta.getStatus());
            entity.setHomeScore(meta.getHomeScore());
            entity.setAwayScore(meta.getAwayScore());
            entity.setHomePitcher(meta.getHomePitcher());
            entity.setAwayPitcher(meta.getAwayPitcher());
        } else {
            entity = Baseball.builder()
                    .time(meta.getGameTime())
                    .weekDay(dateInfo.getWeekday())
                    .home(meta.getHomeTeam())
                    .away(meta.getAwayTeam())
                    .location(meta.getLocation())
                    .status(meta.getStatus())
                    .homeScore(meta.getHomeScore())
                    .awayScore(meta.getAwayScore())
                    .homePitcher(meta.getHomePitcher())
                    .awayPitcher(meta.getAwayPitcher())
                    .build();
        }
        Baseball saved = baseballRepository.save(entity);
        return Optional.of(saved);
    }

    private WebDriver createWebDriver() {
        setUpWebDriver();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
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