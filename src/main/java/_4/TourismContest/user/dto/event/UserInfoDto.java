package _4.TourismContest.user.dto.event;

import _4.TourismContest.user.domain.User;
import lombok.Builder;

@Builder
public record UserInfoDto(
        Long userId,
        String nickname,
        String image,
        String fanTeam
) {

    public static UserInfoDto of(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImg(),
                user.getFanTeam()
        );
    }
}
