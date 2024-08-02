package _4.TourismContest.user.dto;

import _4.TourismContest.user.domain.User;

public record UserProfileResponse(
        String nickname,
        String email,
        String image
) {
    public static UserProfileResponse of(User user){
        return new UserProfileResponse(
                user.getNickname(),
                user.getEmail(),
                user.getProfileImg()
        );
    }
}
