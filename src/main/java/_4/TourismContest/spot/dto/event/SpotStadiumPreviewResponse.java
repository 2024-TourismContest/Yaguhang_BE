package _4.TourismContest.spot.dto.event;

import _4.TourismContest.tour.dto.TourApiResponseDto;

import java.util.ArrayList;
import java.util.List;

public record SpotStadiumPreviewResponse(
        Integer pagesize,
        Integer pageindex,
        String category,
        List<SpotBasicPreviewDto> spotPreviewDtos
) {
    public static SpotStadiumPreviewResponse tourApiToSpotStadiumPreviewResponse(TourApiResponseDto tourApiResponseDtos, String category,
                                                                                 Integer pagesize, Integer pageindex, List<Boolean> scraped){
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
        return new SpotStadiumPreviewResponse(pagesize, pageindex, category,spotBasicPreviewDtos);
    }
}
