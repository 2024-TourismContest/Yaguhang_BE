package _4.TourismContest.spot.dto.command;

import lombok.Builder;

@Builder
public record ScrapSpot(
        Long contentId,
        String image,
        String title
) {
}
