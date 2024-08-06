package _4.TourismContest.user.dto.event;

import lombok.Builder;

@Builder
public record UserDdayDto(
        Long userId,
        Long gameId,
        String nickname,
        String stadium,
        String home,
        String away,
        String dDay,
        String date

) {
}
