package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiRestaurantDetailIntroResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record SpotRestaurantDetailResponse (
        Long contentId,
        String name,
        String address,
        Boolean isScraped,
        String phoneNumber,
        String businessHours,
        String closedDays,
        String description,
        String parkingFacilities,
        String firstmenu,
        String treatmenu,
        List<String> images
) implements SpotDetailResponse {
    public static SpotRestaurantDetailResponse makeSpotRestaurantDetailResponse(TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto,
                                                                                TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto,
                                                                                TourApiDetailImageResponseDto tourApiDetailImageResponseDto, Boolean isScraped){
        TourApiDetailCommonResponseDto.Item commonItem = tourApiDetailCommonResponseDto.getResponse().getBody().getItems().getItem().get(0);
        TourApiRestaurantDetailIntroResponseDto tourApiRestaurantDetailIntroResponseDto = (TourApiRestaurantDetailIntroResponseDto) tourApiDetailIntroResponseDto;
        TourApiRestaurantDetailIntroResponseDto.Item introItem = tourApiRestaurantDetailIntroResponseDto.getResponse().getBody().getItems().getItem().get(0);
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
        SpotRestaurantDetailResponse spotRestaurantDetailResponse = SpotRestaurantDetailResponse.builder()
                .contentId(Long.parseLong(commonItem.getContentid()))
                .name(commonItem.getTitle())
                .address(commonItem.getAddr1() + " " + commonItem.getAddr2())
                .isScraped(isScraped)
                .phoneNumber(introItem.getInfocenterfood())
                .businessHours(introItem.getOpentimefood().replace("<br>", "\n"))
                .closedDays(introItem.getRestdatefood())
                .description(commonItem.getOverview().replace("<br>", "\n"))
                .parkingFacilities(introItem.getParkingfood())
                .treatmenu(introItem.getTreatmenu())
                .firstmenu(introItem.getFirstmenu())
                .images(images)
                .build();

        return spotRestaurantDetailResponse;
    }
}