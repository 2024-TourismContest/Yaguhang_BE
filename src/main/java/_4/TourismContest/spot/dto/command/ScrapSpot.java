package _4.TourismContest.spot.dto.command;

import _4.TourismContest.stadium.dto.StadiumInfo;
import lombok.Builder;

@Builder
public record ScrapSpot(
        StadiumInfo stadiumInfo,
        Long contentId,
        String image,
        String title,
        String category
) {
}
