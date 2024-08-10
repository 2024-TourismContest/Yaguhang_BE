package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiCultureDetailIntroResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record SpotCultureDetailResponse(
        Long contentId,
        String name,
        String address,
        Boolean isScraped,
        String phoneNumber,
        String businessHours,
        String closedDays,
        String description,
        String parkingFacilities,
        String animalZone,
        List<String> images
) implements SpotDetailResponse{
    public static SpotCultureDetailResponse makeSpotCultureDetailResponse(TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto,
                                                                          TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto,
                                                                          TourApiDetailImageResponseDto tourApiDetailImageResponseDto, Boolean isScraped){
        TourApiDetailCommonResponseDto.Item commonItem = tourApiDetailCommonResponseDto.getResponse().getBody().getItems().getItem().get(0);
        TourApiCultureDetailIntroResponseDto tourApiCultureDetailIntroResponseDto = (TourApiCultureDetailIntroResponseDto) tourApiDetailIntroResponseDto;
        TourApiCultureDetailIntroResponseDto.Item introItem = tourApiCultureDetailIntroResponseDto.getResponse().getBody().getItems().getItem().get(0);
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

        SpotCultureDetailResponse spotCultureDetailResponse = SpotCultureDetailResponse.builder()
                .contentId(Long.parseLong(commonItem.getContentid()))
                .name(commonItem.getTitle())
                .address(commonItem.getAddr1() + " " + commonItem.getAddr2())
                .isScraped(isScraped)
                .phoneNumber(introItem.getInfocenterculture())
                .businessHours(introItem.getUsetimeculture())
                .closedDays(introItem.getRestdateculture())
                .description(commonItem.getOverview())
                .parkingFacilities(introItem.getParkingculture())
                .animalZone(introItem.getParkingculture())
                .images(images)
                .build();
        return spotCultureDetailResponse;
    }
}