package _4.TourismContest.spot.dto;

import _4.TourismContest.spot.dto.preview.SpotGeneralPreviewDto;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Builder
public record SpotCategoryResponse(
        String category,
        List<SpotGeneralPreviewDto> spotPreviewDtos
) {
    public static SpotCategoryResponse tourApiToSpotCategoryResponse(TourApiResponseDto tourApiResponseDtos, String category, List<Boolean> scraped){
        // Tour Api 리턴 값으로 DTO 생성 메소드,
        List<SpotGeneralPreviewDto> spotGeneralPreviewDtos = IntStream.range(0, tourApiResponseDtos.getResponse().getBody().getItems().getItem().size())
                .mapToObj(idx -> {
                    TourApiResponseDto.Item item = tourApiResponseDtos.getResponse().getBody().getItems().getItem().get(idx);
                    return SpotGeneralPreviewDto.builder()
                            .contentId(Long.valueOf(item.getContentid()))
                            .name(item.getTitle())
                            .address(item.getAddr1() + item.getAddr2())
                            .imageUrl(item.getFirstimage())
                            .isScraped(scraped.get(idx))
                            .build();
                })
                .collect(Collectors.toList());
        return new SpotCategoryResponse(category, spotGeneralPreviewDtos);
    }
}
