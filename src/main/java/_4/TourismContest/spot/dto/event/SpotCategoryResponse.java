package _4.TourismContest.spot.dto.event;

import _4.TourismContest.tour.dto.TourApiResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
@Builder
public record SpotCategoryResponse(
        String category,
        List<SpotBasicPreviewDto> spotPreviewDtos
) {
    public static SpotCategoryResponse tourApiToSpotCategoryResponse(TourApiResponseDto tourApiResponseDtos, String category){
        // Tour Api 리턴 값으로 DTO 생성 메소드,
        List<SpotBasicPreviewDto> spotBasicPreviewDtos = new ArrayList<>();
        for(TourApiResponseDto.Item item : tourApiResponseDtos.getResponse().getBody().getItems().getItem()){
            SpotBasicPreviewDto spotBasicPreviewDto = SpotBasicPreviewDto.builder()
                    .contentId(Long.valueOf(item.getContentid()))
                    .name(item.getTitle())
                    .address(item.getAddr1() + item.getAddr2())
                    .imageUrl(item.getFirstimage())
                    .build();
            spotBasicPreviewDtos.add(spotBasicPreviewDto);
        }
        return new SpotCategoryResponse(category,spotBasicPreviewDtos);
    }
}
