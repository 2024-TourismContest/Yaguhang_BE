package _4.TourismContest.spot.dto.preview;

import _4.TourismContest.spot.domain.AthletePickSpot;
import _4.TourismContest.spot.domain.Spot;
import lombok.Builder;

@Builder
public record SpotAthletePickPreviewDto(
        Long contentId,
        String name,
        String address,
        String imageUrl,
        String picker,
        Boolean isScraped
) implements SpotBasicPreviewDto{
    public static SpotAthletePickPreviewDto of(AthletePickSpot athletePickSpotsInfo, Boolean isScraped) {
        Spot spot = athletePickSpotsInfo.getSpot();
        return SpotAthletePickPreviewDto.builder()
                .contentId(spot.getId())
                .name(spot.getName())
                .address(athletePickSpotsInfo.getAddress())
                .imageUrl(spot.getImage())
                .picker(athletePickSpotsInfo.getAthleteName())
                .isScraped(isScraped)
                .build();
    }
}
