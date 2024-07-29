package _4.TourismContest.crawler.repository;

import _4.TourismContest.crawler.domain.Baseball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaseballRepository extends JpaRepository<Baseball,Long> {
    @Query("SELECT b FROM Baseball b WHERE b.time = :time AND b.home = :home AND b.away = :away AND b.location = :location")
    Optional<Baseball> findByTimeAndHomeAndAwayAndLocation(@Param("time") LocalDateTime time, @Param("home") String home, @Param("away") String away, @Param("location") String location);
}
