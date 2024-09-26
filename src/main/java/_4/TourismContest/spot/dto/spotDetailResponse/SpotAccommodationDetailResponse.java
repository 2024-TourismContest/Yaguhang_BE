package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiAccommodationDetailIntroResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if(!item.getOriginimgurl().equals("")){
                images.add(item.getOriginimgurl());
            }
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
                .homepage(extractUrl(commonItem.getHomepage()))
                .size(introItem.getRoomcount())
                .parkingFacilities(introItem.getParkinglodging())
                .images(images)
                .build();
        return spotAccommodationDetailResponse;
    }

    public static String extractUrl(String url){
        String urlPattern = "(https?://[\\w./?=&]+)";

        // Pattern 객체 생성
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(url);

        // 매칭되는 URL 찾기
        if (matcher.find()) {
            return  matcher.group(1);  // 첫 번째 매칭된 그룹 반환
        } else {
            return "";
        }
    }
}