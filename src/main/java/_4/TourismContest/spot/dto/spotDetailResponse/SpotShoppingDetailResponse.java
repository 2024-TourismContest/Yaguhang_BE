package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiShoppingDetailIntroResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record SpotShoppingDetailResponse(
        Long contentId,
        String name,
        String address,
        Boolean isScraped,
        String phoneNumber,
        String businessHours,
        String closedDays,
        String description,
        String items,
        String animalZone,
        String parkingFacilities,
        List<String> images
) implements SpotDetailResponse{
    public static SpotShoppingDetailResponse makeSpotShoppingDetailResponse(TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto,
                                                                            TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto,
                                                                            TourApiDetailImageResponseDto tourApiDetailImageResponseDto, Boolean isScraped){
        TourApiDetailCommonResponseDto.Item commonItem = tourApiDetailCommonResponseDto.getResponse().getBody().getItems().getItem().get(0);
        TourApiShoppingDetailIntroResponseDto tourApiShoppingDetailIntroResponseDto = (TourApiShoppingDetailIntroResponseDto) tourApiDetailIntroResponseDto;
        TourApiShoppingDetailIntroResponseDto.Item introItem = tourApiShoppingDetailIntroResponseDto.getResponse().getBody().getItems().getItem().get(0);
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
        SpotShoppingDetailResponse spotShoppingDetailResponse = SpotShoppingDetailResponse.builder()
                .contentId(Long.parseLong(commonItem.getContentid()))
                .name(commonItem.getTitle())
                .address(commonItem.getAddr1() + " " + commonItem.getAddr2())
                .isScraped(isScraped)
                .phoneNumber(introItem.getInfocentershopping())
                .businessHours(introItem.getOpentime().replace("<br>", "\n"))
                .closedDays(introItem.getRestdateshopping())
                .description(commonItem.getOverview().replace("<br>", "\n"))
                .items(introItem.getSaleitem())
                .animalZone(introItem.getChkpetshopping())
                .parkingFacilities(introItem.getParkingshopping())
                .images(images)
                .build();

        return spotShoppingDetailResponse;
    }
}