package _4.TourismContest.baseball.repository.impl;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.user.domain.User;

import java.util.Optional;

public interface BaseballScrapRepositoryCustom {
    Optional<Baseball> findUpcomingBaseballByUser(User user);
}
