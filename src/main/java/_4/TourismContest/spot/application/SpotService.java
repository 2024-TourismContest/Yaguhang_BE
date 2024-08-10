package _4.TourismContest.spot.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.repository.ReviewRepository;
import _4.TourismContest.spot.domain.AthletePickSpot;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.domain.SpotCategory;
import _4.TourismContest.spot.domain.SpotScrap;
import _4.TourismContest.spot.dto.*;
import _4.TourismContest.spot.dto.command.ScrapResponseDto;
import _4.TourismContest.spot.dto.command.ScrapSpot;
import _4.TourismContest.spot.dto.command.ScrapStadium;
import _4.TourismContest.spot.dto.command.ScrapStadiumSpot;
import _4.TourismContest.spot.dto.preview.SpotAthletePickPreviewDto;
import _4.TourismContest.spot.dto.preview.SpotBasicPreviewDto;
import _4.TourismContest.spot.dto.spotDetailResponse.*;
import _4.TourismContest.spot.repository.AthletePickSpotRepository;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.spot.repository.SpotScrapRepository;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import _4.TourismContest.tour.infrastructure.TourApi;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static _4.TourismContest.spot.dto.SpotCategoryResponse.tourApiToSpotCategoryResponse;
import static _4.TourismContest.spot.dto.SpotStadiumPreviewResponse.tourApiToSpotStadiumPreviewResponse;

@Transactional
@AllArgsConstructor
@Service
public class SpotService {
    private final UserRepository userRepository;
    private final SpotScrapRepository spotScrapRepository;
    private final SpotRepository spotRepository;
    private final StadiumRepository stadiumRepository;
    private final ReviewRepository reviewRepository;
    private final TourApi tourApi;
    private final AthletePickSpotRepository athletePickSpotRepository;

    public SpotCategoryResponse getMainSpot(String stadium, String category, UserPrincipal userPrincipal){
        TourApiResponseDto tourApiResponseDto = tourApi.getMainSpot(getCoordinate(stadium), 10000, category); // radius 10km
        return tourApiToSpotCategoryResponse(tourApiResponseDto, category, getIsScrapedList(userPrincipal, tourApiResponseDto));
    }

    public SpotStadiumPreviewResponse getStadiumSpot(Long stadiumId, String category,Integer pagesize, Integer pageindex, Integer radius,UserPrincipal userPrincipal){
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new BadRequestException("잘못된 구장 정보입니다"));
        TourApiResponseDto tourApiResponseDto = tourApi.getStadiumSpot(getCoordinate(stadium.getName()), radius * 1000, category, pagesize);

        return tourApiToSpotStadiumPreviewResponse(tourApiResponseDto, category, pagesize, pageindex, getIsScrapedList(userPrincipal, tourApiResponseDto));
    }

    public SpotStadiumPreviewResponse getAthletePickSpot(Long stadiumId, UserPrincipal userPrincipal){
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new BadRequestException("잘못된 구장 정보입니다"));
        List<Spot> spots = spotRepository.findSpotsByStadiumAndCategory(stadium, SpotCategory.ATHLETE_PICK);

        List<AthletePickSpot> athletePickSpotsInfo = athletePickSpotRepository.findAthletePickSpotsBySpotIn(spots);
        List<SpotBasicPreviewDto> athletePickPreviewDtoList = new ArrayList<>();
        for(AthletePickSpot spot : athletePickSpotsInfo){
            Optional<SpotScrap> scrap = spotScrapRepository.findByUserIdAndSpotContentId(/*userPrincipal.getId()*/1L, spot.getId());
            boolean isScraped = scrap.isPresent();

            athletePickPreviewDtoList.add(SpotAthletePickPreviewDto.of(spot, isScraped));
        }

        return SpotStadiumPreviewResponse.of(athletePickPreviewDtoList);
    }

    public SpotDetailResponse getDetailSpot(String category, Long contentId, UserPrincipal userPrincipal) {
        SpotDetailResponse spotDetailResponse;
        TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto = tourApi.getSpotDetailCommon(contentId);
        TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto = tourApi.getSpotDetailIntro(contentId, category);
        TourApiDetailImageResponseDto tourApiDetailImageResponseDto = tourApi.getSpotDetailImage(contentId);

        if(category.equals("숙소")){
            spotDetailResponse = SpotAccommodationDetailResponse.makeSpotAccommodationDetailResponse(tourApiDetailCommonResponseDto,
                    tourApiDetailIntroResponseDto, tourApiDetailImageResponseDto, getIsScraped(userPrincipal, contentId));
        }
        else if(category.equals("맛집")){
            spotDetailResponse = SpotRestaurantDetailResponse.makeSpotRestaurantDetailResponse(tourApiDetailCommonResponseDto,
                    tourApiDetailIntroResponseDto, tourApiDetailImageResponseDto, getIsScraped(userPrincipal, contentId));
        }
        else if(category.equals("문화")){
            spotDetailResponse = SpotCultureDetailResponse.makeSpotCultureDetailResponse(tourApiDetailCommonResponseDto,
                    tourApiDetailIntroResponseDto, tourApiDetailImageResponseDto, getIsScraped(userPrincipal, contentId));
        }
        else {
            spotDetailResponse = SpotShoppingDetailResponse.makeSpotShoppingDetailResponse(tourApiDetailCommonResponseDto,
                    tourApiDetailIntroResponseDto, tourApiDetailImageResponseDto, getIsScraped(userPrincipal, contentId));
        }

        return spotDetailResponse;
    }

    public Boolean getIsScraped(UserPrincipal userPrincipal, Long contentId){
        if(userPrincipal == null){ // 로그인 정보가 없을 시
            return false;
        }
        else{ // 로그인 정보가 있을 시
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
            if(spotScrapRepository.findByUserIdAndSpotContentId(user.getId(), contentId).isEmpty())
                return false;
            else
                return true;
        }
    }

    public List<Boolean> getIsScrapedList(UserPrincipal userPrincipal, TourApiResponseDto tourApiResponseDto ){
        // 스크랩 여부 가져오기
        List<Boolean> scraped = new ArrayList<>();
        if(userPrincipal == null){ // 로그인 정보가 없을 시
            for(TourApiResponseDto.Item item : tourApiResponseDto.getResponse().getBody().getItems().getItem()){
                scraped.add(false);
            }
        }
        else{ // 로그인 정보가 있을 시
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
            for(TourApiResponseDto.Item item : tourApiResponseDto.getResponse().getBody().getItems().getItem()){
                if(spotScrapRepository.findByUserIdAndSpotContentId(user.getId(), Long.parseLong(item.getContentid())).isEmpty())
                    scraped.add(false);
                else
                    scraped.add(true);
            }
        }

        return scraped;
    }

    public MapXY getCoordinate(String stadiumName){
        // 경기장 좌표값 가져오기
        Stadium stadium = stadiumRepository.findTopByName(stadiumName)
                .orElseThrow(() -> new BadRequestException("경기장 이름을 다시 확인해주세요"));
        return new MapXY(stadium.getX(), stadium.getY());
    }

    public String scrapSpot(Long contentId, Long stadiumId, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
        Optional<SpotScrap> optionalSpotScrap = spotScrapRepository.findByUserIdAndSpotContentId(user.getId(), contentId);
        if(optionalSpotScrap.isPresent()){
            SpotScrap spotScrap = optionalSpotScrap.get();
            spotScrapRepository.delete(spotScrap);
            return "remove scrap";
        }
        else{
            Stadium stadium = stadiumRepository.findById(stadiumId)
                    .orElseThrow(() -> new BadRequestException("stadiumId를 다시 확인해주세요"));
            TourApiDetailCommonResponseDto.Item tourApiDetailCommonResponseDto = tourApi.getSpotDetailCommon(contentId).getResponse().getBody().getItems().getItem().get(0);
            Spot spot = Spot.builder()
                    .contentId(contentId)
                    .stadium(stadium)
                    .name(tourApiDetailCommonResponseDto.getTitle())
                    .mapX(Double.parseDouble(tourApiDetailCommonResponseDto.getMapx()))
                    .mapY(Double.parseDouble(tourApiDetailCommonResponseDto.getMapy()))
                    .image(tourApiDetailCommonResponseDto.getFirstimage())
                    .build();
            SpotScrap spotScrap = SpotScrap.builder()
                    .user(user)
                    .spot(spotRepository.save(spot))
                    .build();
            spotScrapRepository.save(spotScrap);
            return "add scrap";
        }
    }

    public ScrapResponseDto getMyScrap(UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));

        List<ScrapStadiumSpot> scrapStadiumSpots = new ArrayList<>();

        ScrapStadiumSpot scrapStadiumSpot;
        scrapStadiumSpot = getScrapStadiumSpot(user, "잠실");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "수원");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "문학");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "창원");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "광주");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "사직");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "대구");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "대전");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }
        scrapStadiumSpot = getScrapStadiumSpot(user, "고척");
        if(scrapStadiumSpot != null){
            scrapStadiumSpots.add(scrapStadiumSpot);
        }

        ScrapResponseDto scrapResponseDto = ScrapResponseDto.builder()
                .scrapStadiumSpots(scrapStadiumSpots)
                .build();

        return scrapResponseDto;
    }

    public ScrapStadiumSpot getScrapStadiumSpot(User user, String name){
        List<SpotScrap> spotScraps = spotScrapRepository.findByUserIdAndName(user.getId(), name);
        if (spotScraps == null || spotScraps.isEmpty()) {
            return null;
        }

        Stadium stadium = spotScraps.get(0).getSpot().getStadium();

        ScrapStadium scrapStadium = ScrapStadium.builder()
                .stadiumId(stadium.getId())
                .title(stadium.getName() + " 야구장")
                .image(stadium.getImage())
                .build();

        List<ScrapSpot> scrapSpots = spotScraps.stream()
                .map(spotScrap -> ScrapSpot.builder()
                        .contentId(spotScrap.getSpot().getId())
                        .title(spotScrap.getSpot().getName())
                        .image(spotScrap.getSpot().getImage())
                        .build())
                .collect(Collectors.toList());

        ScrapStadiumSpot scrapStadiumSpot = ScrapStadiumSpot.builder()
                .scrapStadium(scrapStadium)
                .scrapSpots(scrapSpots)
                .build();

        return scrapStadiumSpot;
    }

    public List<SpotMapResponseDto> getNearSpot(double nowX, double nowY, Long stadiumId, String category, int level, UserPrincipal userPrincipal) throws IOException {
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 구장 ID 입니다."));
        long startTime = System.nanoTime();

        int radius = getRadius(level);
        MapXY stadiumCoordinate = getCoordinate(stadium.getName());  // 경기장 좌표
        double distanceToStadium = calculateDistance(nowY, nowX, stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue());
        int pageSize = 30;

        List<SpotMapResponseDto> filteredItems = new ArrayList<>();

        if (distanceToStadium > radius + 20) {
            // 두 원이 서로 밖에 있으며 만나지 않는 경우 -> API 호출 X
            return filteredItems;
        }

        TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius * 1000, tourApi.getContentTypeId(category), pageSize)
                .getResponse().getBody().getItems();

        for (TourApiResponseDto.Item item : items.getItem()) {
            double itemLatitude = Double.parseDouble(item.getMapy());
            double itemLongitude = Double.parseDouble(item.getMapx());
            double distance = calculateDistance(stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue(), itemLatitude, itemLongitude);
            if (distanceToStadium <= Math.abs(radius - 20)) {

                Optional<Spot> spotOptional = spotRepository.findById(Long.parseLong(item.getContentid()));
                Long reviewCount = 0L;
                if(spotOptional.isPresent()){
                    Spot spot = spotOptional.get();
                    reviewCount = Long.valueOf(reviewRepository.findAllBySpot(spot).size());
                }
                // 두 원이 내접하거나 포함되는 경우 -> 전체 API 호출 결과 반환
                filteredItems.add(SpotMapResponseDto.builder()
                        .contentId(Long.parseLong(item.getContentid()))
                        .stadiumId(stadium.getId())
                        .title(item.getTitle())
                        .address(item.getAddr1()+item.getAddr2())
                        .mapX(Double.parseDouble(item.getMapx()))
                        .mapY(Double.parseDouble(item.getMapy()))
                        .reviewCount(reviewCount)
                        .image(item.getFirstimage())
                        .build());
            } else if (distance <= 20) {

                Optional<Spot> spotOptional = spotRepository.findById(Long.parseLong(item.getContentid()));
                Long reviewCount = 0L;
                if(spotOptional.isPresent()){
                    Spot spot = spotOptional.get();
                    reviewCount = Long.valueOf(reviewRepository.findAllBySpot(spot).size());
                }
                // 두 원이 내접하거나 포함되는 경우 -> 전체 API 호출 결과 반환
                filteredItems.add(SpotMapResponseDto.builder()
                        .contentId(Long.parseLong(item.getContentid()))
                        .stadiumId(stadium.getId())
                        .title(item.getTitle())
                        .address(item.getAddr1()+item.getAddr2())
                        .mapX(Double.parseDouble(item.getMapx()))
                        .mapY(Double.parseDouble(item.getMapy()))
                        .reviewCount(reviewCount)
                        .image(item.getFirstimage())
                        .build());;
            }
        }

        // 사용자 정보가 있을 경우, 스크랩 여부를 추가로 표시
        if (userPrincipal != null) {
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("JWT 토큰을 확인하세요"));
            Optional<List<SpotScrap>> allByUser = spotScrapRepository.findAllByUser(user);

            if (allByUser.isPresent()) {
                Set<Long> contentSet = allByUser.get().stream()
                        .map(scrap -> scrap.getSpot().getId())
                        .collect(Collectors.toSet());

                // 기존 리스트를 새 리스트로 변환하면서 스크랩 여부 추가
                filteredItems = filteredItems.stream()
                        .map(dto -> SpotMapResponseDto.builder()
                                .contentId(dto.contentId())
                                .mapX(dto.mapX())
                                .mapY(dto.mapY())
                                .title(dto.title())
                                .isScrapped(contentSet.contains(dto.contentId().longValue())) // 스크랩 여부 설정
                                .build())
                        .collect(Collectors.toList());
            }
        }


        long endTime = System.nanoTime();
        long durationInMillis = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time: " + durationInMillis + " ms");

        return filteredItems;
    }

    private int getRadius(int level) {
        switch (level) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return 20;
            case 6:
                return 18;
            case 7:
                return 10;
            case 8:
                return 6;
            case 9:
                return 3;
            case 10:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    // Haversine formula를 사용하여 두 좌표 간의 거리를 계산하는 메서드
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 지구 반경 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // 두 좌표 간의 거리 반환
    }

    @Transactional
    public SpotDetailInfoDto getNearSpotDetailInfo(Long stadiumId, Long contentId, UserPrincipal userPrincipal) {
        Stadium stadium = stadiumRepository.findById(stadiumId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 구장 ID입니다."));
        TourApiDetailCommonResponseDto.Item item = tourApi.getSpotDetailCommon(contentId)
                .getResponse().getBody().getItems().getItem().get(0);

        Spot spot = spotRepository.findById(contentId)
                .orElseGet(() -> createAndSaveSpot(contentId, stadium.getName(), item));

        List<Review> reviews = reviewRepository.findAllBySpot(spot);
        int reviewCount = reviews.size();

        boolean isScrapped = userPrincipal != null && isSpotScrappedByUser(userPrincipal, contentId);

        return buildSpotDetailInfoDto(stadium.getName(), contentId, item, reviewCount, isScrapped);
    }

    private Spot createAndSaveSpot(Long contentId, String stadiumName, TourApiDetailCommonResponseDto.Item item) {
        Stadium stadium = stadiumRepository.findTopByName(stadiumName)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 구장명입니다."));
        Spot spot = Spot.builder()
                .contentId(contentId)
                .stadium(stadium)
                .name(item.getTitle())
                .mapX(Double.parseDouble(item.getMapx()))
                .mapY(Double.parseDouble(item.getMapy()))
                .image(item.getFirstimage())
                .build();
        return spotRepository.save(spot);
    }

    private boolean isSpotScrappedByUser(UserPrincipal userPrincipal, Long contentId) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("JWT 토큰을 확인해주세요"));
        return spotScrapRepository.findAllByUser(user)
                .orElse(Collections.emptyList()).stream()
                .anyMatch(scrap -> scrap.getSpot().getId().equals(contentId));
    }

    private SpotDetailInfoDto buildSpotDetailInfoDto(String stadium, Long contentId, TourApiDetailCommonResponseDto.Item item, int reviewCount, boolean isScrapped) {
        Stadium stadium1 = stadiumRepository.findTopByName(stadium)
                .orElseThrow(() -> new BadRequestException("구장 이름이 잘못 되어있습니다."));
        return SpotDetailInfoDto.builder()
                .contentId(contentId)
                .stadiumId(stadium1.getId())
                .isScraped(isScrapped)
                .title(item.getTitle())
                .address(item.getAddr1() + item.getAddr2())
                .mapX(Double.parseDouble(item.getMapx()))
                .mapY(Double.parseDouble(item.getMapy()))
                .image(item.getFirstimage())
                .description(item.getOverview())
                .reviewCount(reviewCount)
                .build();
    }

}

