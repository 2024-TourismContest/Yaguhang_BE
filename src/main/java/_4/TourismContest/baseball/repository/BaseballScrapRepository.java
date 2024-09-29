package _4.TourismContest.baseball.repository;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.domain.BaseballScrap;
import _4.TourismContest.baseball.repository.impl.BaseballScrapRepositoryCustom;
import _4.TourismContest.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseballScrapRepository extends JpaRepository<BaseballScrap, Long>, BaseballScrapRepositoryCustom {
    Optional<BaseballScrap> findByBaseballAndUser(Baseball baseball, User user);
    Page<BaseballScrap> findByUser(User user, Pageable pageable);
    void deleteAllByUser(User user);
}
