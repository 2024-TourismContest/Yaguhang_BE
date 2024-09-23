package _4.TourismContest.user.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 30)
        String nickname,
        String profileImge
) {

}