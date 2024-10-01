package _4.TourismContest.baseball.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.domain.BaseballScrap;
import _4.TourismContest.baseball.dto.BaseBallDTO;
import _4.TourismContest.baseball.dto.BaseBallScrapResponseDTO;
import _4.TourismContest.baseball.dto.ScrappedBaseballDTO;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import _4.TourismContest.weather.application.WeatherForecastService;
import _4.TourismContest.weather.domain.enums.WeatherForecastEnum;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseballScrapService {
    private final UserRepository userRepository;
    private final BaseballScrapRepository baseballScrapRepository;
    private final BaseballRepository baseballRepository;
    private final WeatherForecastService weatherForecastService;

    /**
     * 경기 스크랩 여부
     * @param userPrincipal
     * @param gameId
     * @return
     */
    public Boolean getIsScrapped(UserPrincipal userPrincipal, Long gameId){
        if(userPrincipal == null){
            //로그인 정보가 없을 시
            return false;
        }else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
            Baseball baseball = baseballRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("경기 ID를 다시 확인해주세요"));
            Optional<BaseballScrap> baseballScrapOptional = baseballScrapRepository.findByBaseballAndUser(baseball, user);
            if(baseballScrapOptional.isEmpty()){
                return false;
            }else{
                return true;
            }
        }
    }

    /**
     * 경기 스크랩하기
     * 만약 기존에 스크랩 되어 있던 경우 -> 스크랩 취소
     * 스크랩 되어 있지 않는 경우 -> 스크랩
     * @param userPrincipal
     * @param gameId
     * @return
     */
    @Transactional
    public String scrapSchdule(UserPrincipal userPrincipal, Long gameId) {
        if (userPrincipal == null) {
            throw new BadRequestException("유저 토큰 값을 넣어주세요");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));

        Baseball baseball = baseballRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("경기 ID를 다시 확인해주세요"));

        Optional<BaseballScrap> baseballScrapOptional = baseballScrapRepository.findByBaseballAndUser(baseball, user);

        if (baseballScrapOptional.isPresent()) {
            baseballScrapRepository.delete(baseballScrapOptional.get());

            return "remove scrap";
        }
        else{// 새롭게 스크랩을 할 경우
            BaseballScrap baseballScrap = BaseballScrap.builder()
                    .user(user)
                    .baseball(baseball)
                    .build();

            baseballScrapRepository.save(baseballScrap);

            return "add scrap";
        }
    }


    public ScrappedBaseballDTO getScrappedBaseballGamesList(UserPrincipal userPrincipal, int page, int size) {
        if (userPrincipal == null) {
            throw new BadRequestException("JWT 토큰을 입력해주세요");
        } else {
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("JWT 토큰값을 다시 확인해주세요"));

//            LocalDateTime now = LocalDateTime.now();

            LocalDateTime.of(2024, 9, 20, 0, 0)
            List<BaseBallDTO> baseBallDTOList = baseballScrapRepository.findByUser(user, PageRequest.of(page, size))
                    .getContent().stream()
                    .filter(baseballScrap -> baseballScrap.getBaseball().getTime().isAfter(now))
                    .sorted(Comparator.comparing(baseballScrap -> baseballScrap.getBaseball().getTime()))
                    .map(baseballScrap -> {
                        Baseball baseball = baseballScrap.getBaseball();
                        WeatherForecastEnum weather = weatherForecastService.getWeatherForecastDataWithGame(baseball);

                        return BaseBallDTO.builder()
                                .id(baseball.getId())
                                .home(baseball.getHome())
                                .homeTeamLogo(getTeamLogoUrl(exchangeTeamName(baseball.getHome())))
                                .away(baseball.getAway())
                                .awayTeamLogo(getTeamLogoUrl(exchangeTeamName(baseball.getAway())))
                                .stadium(baseball.getLocation())
                                .date(baseball.getTime().toLocalDate().toString())
                                .time(baseball.getTime().toLocalTime().toString())
                                .weather(weather)
                                .weatherUrl(getWeatherUrl(weather))
                                .isScraped(true)
                                .build();
                    }).collect(Collectors.toList());

            return ScrappedBaseballDTO.builder()
                    .pageIndex(page)
                    .pageSize(size)
                    .scrappedSchedules(baseBallDTOList)
                    .build();
        }
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

    private String getTeamLogoUrl(String team) {
        String baseUrl = "https://yaguhang.kro.kr:8443/teamLogos/";

        switch (team) {
            case "두산 베어스":
                return baseUrl + "Doosan.png";
            case "LG 트윈스":
                return baseUrl + "LGTwins.png";
            case "KT 위즈":
                return baseUrl + "KtWizs.png";
            case "SSG 랜더스":
                return baseUrl + "SSGLanders.png";
            case "NC 다이노스":
                return baseUrl + "NCDinos.png";
            case "KIA 타이거즈":
                return baseUrl + "KIA.png";
            case "롯데 자이언츠":
                return baseUrl + "Lotte.png";
            case "삼성 라이온즈":
                return baseUrl + "Samsung.png";
            case "한화 이글스":
                return baseUrl + "Hanwha.png";
            case "키움 히어로즈":
                return baseUrl + "Kiwoom.png";
            default:
                throw new IllegalArgumentException("Unknown team: " + team);
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
