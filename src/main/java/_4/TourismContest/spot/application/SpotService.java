package _4.TourismContest.spot.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.dto.event.SpotStadiumPreviewResponse;
import _4.TourismContest.spot.dto.event.spotDetailResponse.*;
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
import java.util.ArrayList;
import java.util.List;

import static _4.TourismContest.spot.dto.event.SpotCategoryResponse.tourApiToSpotCategoryResponse;
import static _4.TourismContest.spot.dto.event.SpotStadiumPreviewResponse.tourApiToSpotStadiumPreviewResponse;

@Transactional
@AllArgsConstructor
@Service
public class SpotService {
    private final UserRepository userRepository;
    private final SpotScrapRepository spotScrapRepository;
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
            if(spotScrapRepository.findByUserAndContentId(user, contentId).isEmpty())
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
                if(spotScrapRepository.findByUserAndContentId(user, Long.parseLong(item.getContentid())).isEmpty())
                    scraped.add(false);
                else
                    scraped.add(true);
            }
        }

        return scraped;
    }

    public MapXY getCoordinate(String stadiumName){
        // 경기장 좌표값 가져오기
        Stadium stadium = stadiumRepository.findByName(stadiumName)
                .orElseThrow(() -> new BadRequestException("경기장 이름을 다시 확인해주세요"));
        return new MapXY(stadium.getX(), stadium.getY());
    }


}
