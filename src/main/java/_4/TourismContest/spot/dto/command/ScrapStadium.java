package _4.TourismContest.spot.dto.command;

import lombok.Builder;

@Builder
public record ScrapStadium(
        Long stadiumId,
        String image,
        String title
) {
}
