package _4.TourismContest.spot.dto.event;

import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record SpotCategoryResponse(
        String category,
        List<SpotBasicPreviewDto> spotPreviewDtos
) {
    public static SpotCategoryResponse tourApiToSpotCategoryResponse(TourApiResponseDto tourApiResponseDtos, String category, List<Boolean> scraped){
        // Tour Api 리턴 값으로 DTO 생성 메소드,
        List<SpotBasicPreviewDto> spotBasicPreviewDtos = new ArrayList<>();
        int idx = 0;
        for(TourApiResponseDto.Item item : tourApiResponseDtos.getResponse().getBody().getItems().getItem()){
            SpotBasicPreviewDto spotBasicPreviewDto = SpotBasicPreviewDto.builder()
                    .contentId(Long.valueOf(item.getContentid()))
                    .name(item.getTitle())
                    .address(item.getAddr1() + item.getAddr2())
                    .imageUrl(item.getFirstimage())
                    .isScraped(scraped.get(idx++))
                    .build();
            spotBasicPreviewDtos.add(spotBasicPreviewDto);
        }
        return new SpotCategoryResponse(category,spotBasicPreviewDtos);
    }
}
