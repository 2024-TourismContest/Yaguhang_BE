package _4.TourismContest.spot.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotBasicPreviewDto;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.spot.repository.SpotScrapRepository;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import _4.TourismContest.tour.infrastructure.TourApi;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static _4.TourismContest.spot.dto.event.SpotCategoryResponse.tourApiToSpotCategoryResponse;

@AllArgsConstructor
@Service
public class SpotService {
    private final UserRepository userRepository;
    private final SpotScrapRepository spotScrapRepository;
    private final StadiumRepository stadiumRepository;
    private final TourApi tourApi;

    public SpotCategoryResponse getMainSpot(String stadium, String category, UserPrincipal userPrincipal){
        int radius = 10000; // 10km로 고정
        TourApiResponseDto tourApiResponseDto = tourApi.getMainSpot(getCoordinate(stadium), radius, category);
        return tourApiToSpotCategoryResponse(tourApiResponseDto, category, getIsScraped(userPrincipal, tourApiResponseDto));
    }

    public List<Boolean> getIsScraped(UserPrincipal userPrincipal, TourApiResponseDto tourApiResponseDto ){
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
