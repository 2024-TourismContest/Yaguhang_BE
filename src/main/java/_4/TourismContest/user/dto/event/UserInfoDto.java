package _4.TourismContest.user.dto.event;

import lombok.Builder;

@Builder
public record UserInfoDto(
        Long userId,
        String nickname,
        String image,
        String fanTeam
) {

}
