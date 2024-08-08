package _4.TourismContest.spot.dto.event;

import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
public record SpotCategoryResponse(
        String category,
        List<SpotBasicPreviewDto> spotPreviewDtos
) {
    public static SpotCategoryResponse tourApiToSpotCategoryResponse(TourApiResponseDto tourApiResponseDtos, String category, List<Boolean> scraped){
        // Tour Api 리턴 값으로 DTO 생성 메소드,
        List<SpotBasicPreviewDto> spotBasicPreviewDtos = IntStream.range(0, tourApiResponseDtos.getResponse().getBody().getItems().getItem().size())
                .mapToObj(idx -> {
                    TourApiResponseDto.Item item = tourApiResponseDtos.getResponse().getBody().getItems().getItem().get(idx);
                    return SpotBasicPreviewDto.builder()
                            .contentId(Long.valueOf(item.getContentid()))
                            .name(item.getTitle())
                            .address(item.getAddr1() + item.getAddr2())
                            .imageUrl(item.getFirstimage())
                            .isScraped(scraped.get(idx))
                            .build();
                })
                .collect(Collectors.toList());
        return new SpotCategoryResponse(category,spotBasicPreviewDtos);
    }
}
