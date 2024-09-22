package _4.TourismContest.recommend.dto.event;

import lombok.Builder;

@Builder
public record ScrapAddressSpot(
        Long contentId,
        String image,
        String title,
        String address,
        String categoryLogo
) {
}
