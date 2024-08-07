package _4.TourismContest.spot.dto.command;

import lombok.Builder;

import java.util.List;
@Builder
public record ScrapResponseDto(
        List<ScrapStadiumSpot> scrapStadiumSpots
) {
}
