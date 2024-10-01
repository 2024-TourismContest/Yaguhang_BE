package _4.TourismContest.baseball.repository;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.domain.BaseballScrap;
import _4.TourismContest.baseball.repository.impl.BaseballScrapRepositoryCustom;
import _4.TourismContest.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BaseballScrapRepository extends JpaRepository<BaseballScrap, Long>, BaseballScrapRepositoryCustom {
    Optional<BaseballScrap> findByBaseballAndUser(Baseball baseball, User user);
    Page<BaseballScrap> findByUser(User user, Pageable pageable);
    void deleteAllByUser(User user);

    @Query("SELECT bs FROM BaseballScrap bs WHERE bs.baseball.time BETWEEN :startOfDay AND :endOfDay")
    List<BaseballScrap> findBaseballByDate(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
