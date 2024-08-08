package _4.TourismContest.spot.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.domain.SpotScrap;
import _4.TourismContest.spot.dto.command.ScrapResponseDto;
import _4.TourismContest.spot.dto.command.ScrapSpot;
import _4.TourismContest.spot.dto.command.ScrapStadium;
import _4.TourismContest.spot.dto.command.ScrapStadiumSpot;
import _4.TourismContest.spot.dto.event.*;
import _4.TourismContest.spot.dto.event.spotDetailResponse.*;
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
import java.time.LocalDateTime;
import java.util.*;

import static _4.TourismContest.spot.dto.event.SpotCategoryResponse.tourApiToSpotCategoryResponse;
import static _4.TourismContest.spot.dto.event.SpotStadiumPreviewResponse.tourApiToSpotStadiumPreviewResponse;

@Transactional
@AllArgsConstructor
@Service
public class SpotService {
    private final UserRepository userRepository;
    private final SpotScrapRepository spotScrapRepository;
    private final SpotRepository spotRepository;
    private final StadiumRepository stadiumRepository;
    private final TourApi tourApi;

    public SpotCategoryResponse getMainSpot(String stadium, String category, UserPrincipal userPrincipal){
        TourApiResponseDto tourApiResponseDto = tourApi.getMainSpot(getCoordinate(stadium), 10000, category); // radius 10km
        return tourApiToSpotCategoryResponse(tourApiResponseDto, category, getIsScrapedList(userPrincipal, tourApiResponseDto));
    }

    public SpotStadiumPreviewResponse getStadiumSpot(String stadium, String category,Integer pagesize, Integer pageindex,UserPrincipal userPrincipal){
        TourApiResponseDto tourApiResponseDto = tourApi.getStadiumSpot(getCoordinate(stadium), 20000, category, pagesize); // radius 20km

        return tourApiToSpotStadiumPreviewResponse(tourApiResponseDto, category, pagesize, pageindex, getIsScrapedList(userPrincipal, tourApiResponseDto));
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

        List<ScrapSpot> scrapSpots = new ArrayList<>();

        for(SpotScrap spotScrap : spotScraps){
            ScrapSpot scrapSpot = ScrapSpot.builder()
                    .contentId(spotScrap.getSpot().getContentId())
                    .title(spotScrap.getSpot().getName())
                    .image(spotScrap.getSpot().getImage())
                    .build();
            scrapSpots.add(scrapSpot);
        }

        ScrapStadiumSpot scrapStadiumSpot = ScrapStadiumSpot.builder()
                .scrapStadium(scrapStadium)
                .scrapSpots(scrapSpots)
                .build();

        return scrapStadiumSpot;
    }


 public List<SpotMapResponseDto> getNearSpot(double nowX, double nowY, String stadium, String category, int radius, UserPrincipal userPrincipal) throws IOException {
        // 시작 시간 측정
        long startTime = System.nanoTime();

        if(userPrincipal == null){
            MapXY stadiumCoordinate = getCoordinate(stadium);   // 경기장 좌표

            // 사용자와 경기장 간의 거리 계산 (단위: km)
            double distanceToStadium = calculateDistance(nowY, nowX, stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue());
            int pageSize = 30;

            List<SpotMapResponseDto> filteredItems = new ArrayList<>();

            // 1. 두 원이 서로 밖에 있으며 만나지 않는 경우 -> API 호출 X
            if (distanceToStadium > radius + 20) {
                // Do nothing
            }
            // 2. 외접하는 경우 (하나의 점에서 만남) -> API 호출 X
            else if (distanceToStadium == radius + 20) {
                // Do nothing
            }
            // 3. 두 점에서 만나는 경우 -> 겹치는 부분만 API 호출하여 필터링
            else if (distanceToStadium > Math.abs(radius - 20)) {
                TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius, tourApi.getContentTypeId(category), pageSize)
                        .getResponse().getBody().getItems();

                for (TourApiResponseDto.Item item : items.getItem()) {
                    double itemLatitude = Double.parseDouble(item.getMapy());
                    double itemLongitude = Double.parseDouble(item.getMapx());

                    double distance = calculateDistance(stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue(), itemLatitude, itemLongitude);

                    if (distance <= 20) {
                        SpotMapResponseDto build = SpotMapResponseDto.builder()
                                .contentId(Integer.parseInt(item.getContentid()))
                                .mapX(Double.parseDouble(item.getMapx()))
                                .mapY(Double.parseDouble(item.getMapy()))
                                .build();
                        filteredItems.add(build);
                    }
                }
            }
            // 4. 내접하거나 포함되는 경우 -> 전체 API 호출 결과 반환
            else {
                TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius * 1000, tourApi.getContentTypeId(category), pageSize)
                        .getResponse().getBody().getItems();

                for (TourApiResponseDto.Item item : items.getItem()) {
                    SpotMapResponseDto dto = SpotMapResponseDto.builder()
                            .contentId(Integer.parseInt(item.getContentid()))
                            .mapX(Double.parseDouble(item.getMapx()))
                            .mapY(Double.parseDouble(item.getMapy()))
                            .name(item.getTitle())
                            .build();
                    filteredItems.add(dto);
                }
            }

            // 끝 시간 측정
            long endTime = System.nanoTime();

            // 실행 시간 계산 (밀리초 단위로 변환)
            long durationInMillis = (endTime - startTime) / 1_000_000;
            System.out.println("Execution time: " + durationInMillis + " ms");

            return filteredItems;
        }else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("JWT 토큰을 확인하세요"));

            Optional<List<SpotScrap>> allByUser = spotScrapRepository.findAllByUser(user);
            if(allByUser.isEmpty()){
                //스크랩 한 데이터가 없을 경우
                MapXY stadiumCoordinate = getCoordinate(stadium);   // 경기장 좌표

                // 사용자와 경기장 간의 거리 계산 (단위: km)
                double distanceToStadium = calculateDistance(nowY, nowX, stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue());
                int pageSize = 30;

                List<SpotMapResponseDto> filteredItems = new ArrayList<>();

                // 1. 두 원이 서로 밖에 있으며 만나지 않는 경우 -> API 호출 X
                if (distanceToStadium > radius + 20) {
                    // Do nothing
                }
                // 2. 외접하는 경우 (하나의 점에서 만남) -> API 호출 X
                else if (distanceToStadium == radius + 20) {
                    // Do nothing
                }
                // 3. 두 점에서 만나는 경우 -> 겹치는 부분만 API 호출하여 필터링
                else if (distanceToStadium > Math.abs(radius - 20)) {
                    TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius, tourApi.getContentTypeId(category), pageSize)
                            .getResponse().getBody().getItems();

                    for (TourApiResponseDto.Item item : items.getItem()) {
                        double itemLatitude = Double.parseDouble(item.getMapy());
                        double itemLongitude = Double.parseDouble(item.getMapx());

                        double distance = calculateDistance(stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue(), itemLatitude, itemLongitude);

                        if (distance <= 20) {
                            SpotMapResponseDto build = SpotMapResponseDto.builder()
                                    .contentId(Integer.parseInt(item.getContentid()))
                                    .mapX(Double.parseDouble(item.getMapx()))
                                    .mapY(Double.parseDouble(item.getMapy()))
                                    .build();
                            filteredItems.add(build);
                        }
                    }
                }
                // 4. 내접하거나 포함되는 경우 -> 전체 API 호출 결과 반환
                else {
                    TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius * 1000, tourApi.getContentTypeId(category), pageSize)
                            .getResponse().getBody().getItems();

                    for (TourApiResponseDto.Item item : items.getItem()) {
                        SpotMapResponseDto dto = SpotMapResponseDto.builder()
                                .contentId(Integer.parseInt(item.getContentid()))
                                .mapX(Double.parseDouble(item.getMapx()))
                                .mapY(Double.parseDouble(item.getMapy()))
                                .name(item.getTitle())
                                .build();
                        filteredItems.add(dto);
                    }
                }

                // 끝 시간 측정
                long endTime = System.nanoTime();

                // 실행 시간 계산 (밀리초 단위로 변환)
                long durationInMillis = (endTime - startTime) / 1_000_000;
                System.out.println("Execution time: " + durationInMillis + " ms");

                return filteredItems;
            }else{
                //스크랩 한 데이터가 있을 경우
                List<SpotScrap> spotScraps = allByUser.get();
                Set<Long> contentSet = new HashSet<>();
                for (SpotScrap spotScrap : spotScraps) {
                    contentSet.add(spotScrap.getSpot().getContentId());
                }
                MapXY stadiumCoordinate = getCoordinate(stadium);   // 경기장 좌표

                // 사용자와 경기장 간의 거리 계산 (단위: km)
                double distanceToStadium = calculateDistance(nowY, nowX, stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue());
                int pageSize = 30;

                List<SpotMapResponseDto> filteredItems = new ArrayList<>();

                // 1. 두 원이 서로 밖에 있으며 만나지 않는 경우 -> API 호출 X
                if (distanceToStadium > radius + 20) {
                    // Do nothing
                }
                // 2. 외접하는 경우 (하나의 점에서 만남) -> API 호출 X
                else if (distanceToStadium == radius + 20) {
                    // Do nothing
                }
                // 3. 두 점에서 만나는 경우 -> 겹치는 부분만 API 호출하여 필터링
                else if (distanceToStadium > Math.abs(radius - 20)) {
                    TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius, tourApi.getContentTypeId(category), pageSize)
                            .getResponse().getBody().getItems();

                    for (TourApiResponseDto.Item item : items.getItem()) {
                        double itemLatitude = Double.parseDouble(item.getMapy());
                        double itemLongitude = Double.parseDouble(item.getMapx());

                        double distance = calculateDistance(stadiumCoordinate.y().doubleValue(), stadiumCoordinate.x().doubleValue(), itemLatitude, itemLongitude);

                        if (distance <= 20) {
                            SpotMapResponseDto build = SpotMapResponseDto.builder()
                                    .contentId(Integer.parseInt(item.getContentid()))
                                    .mapX(Double.parseDouble(item.getMapx()))
                                    .mapY(Double.parseDouble(item.getMapy()))
                                    .name(item.getTitle())
                                    .isScrapped(contentSet.contains(Long.parseLong(item.getContentid())))
                                    .build();
                            filteredItems.add(build);
                        }
                    }
                }
                // 4. 내접하거나 포함되는 경우 -> 전체 API 호출 결과 반환
                else {
                    TourApiResponseDto.Items items = tourApi.getSpot((float) nowX, (float) nowY, radius * 1000, tourApi.getContentTypeId(category), pageSize)
                            .getResponse().getBody().getItems();

                    for (TourApiResponseDto.Item item : items.getItem()) {
                        SpotMapResponseDto dto = SpotMapResponseDto.builder()
                                .contentId(Integer.parseInt(item.getContentid()))
                                .mapX(Double.parseDouble(item.getMapx()))
                                .mapY(Double.parseDouble(item.getMapy()))
                                .name(item.getTitle())
                                .isScrapped(contentSet.contains(Long.parseLong(item.getContentid())))
                                .build();
                        filteredItems.add(dto);
                    }
                }

                // 끝 시간 측정
                long endTime = System.nanoTime();

                // 실행 시간 계산 (밀리초 단위로 변환)
                long durationInMillis = (endTime - startTime) / 1_000_000;
                System.out.println("Execution time: " + durationInMillis + " ms");

                return filteredItems;
            }
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
}
