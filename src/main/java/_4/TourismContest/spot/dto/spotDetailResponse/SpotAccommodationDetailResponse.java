package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiAccommodationDetailIntroResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record SpotAccommodationDetailResponse (
        Long stadiumId,
        Long contentId,
        String name,
        String address,
        Boolean isScraped,
        String phoneNumber,
        String description,
        String checkIn,
        String checkOut,
        String homepage,
        String size,
        String parkingFacilities,
        List<String> images
) implements SpotDetailResponse {
    public static SpotAccommodationDetailResponse makeSpotAccommodationDetailResponse(TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto,
                                                                                      TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto,
                                                                                      TourApiDetailImageResponseDto tourApiDetailImageResponseDto, Boolean isScraped, Long stadiumId){
        TourApiDetailCommonResponseDto.Item commonItem = tourApiDetailCommonResponseDto.getResponse().getBody().getItems().getItem().get(0);
        TourApiAccommodationDetailIntroResponseDto tourApiAccommodationDetailIntroResponseDto = (TourApiAccommodationDetailIntroResponseDto) tourApiDetailIntroResponseDto;
        TourApiAccommodationDetailIntroResponseDto.Item introItem = tourApiAccommodationDetailIntroResponseDto.getResponse().getBody().getItems().getItem().get(0);
        TourApiDetailImageResponseDto.Items ImageItems = tourApiDetailImageResponseDto.getResponse().getBody().getItems();
        List<String> images = new ArrayList<>();
        if(commonItem.getFirstimage() != null){
            images.add(commonItem.getFirstimage());
        }
//        if(commonItem.getFirstimage2() != null){
//            images.add(commonItem.getFirstimage2());
//        }
        for(TourApiDetailImageResponseDto.Item item : ImageItems.getItem()){
            images.add(item.getOriginimgurl());
        }

        SpotAccommodationDetailResponse spotAccommodationDetailResponse = SpotAccommodationDetailResponse.builder()
                .stadiumId(stadiumId)
                .contentId(Long.parseLong(commonItem.getContentid()))
                .name(commonItem.getTitle())
                .address(commonItem.getAddr1() + " " + commonItem.getAddr2())
                .isScraped(isScraped)
                .phoneNumber(introItem.getInfocenterlodging())
                .description(commonItem.getOverview().replace("<br>", "\n"))
                .checkIn(introItem.getCheckintime())
                .checkOut(introItem.getCheckouttime())
                .homepage(commonItem.getHomepage())
                .size(introItem.getRoomcount())
                .parkingFacilities(introItem.getParkinglodging())
                .images(images)
                .build();
        return spotAccommodationDetailResponse;
    }
}