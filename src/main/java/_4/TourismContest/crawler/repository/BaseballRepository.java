package _4.TourismContest.crawler.repository;

import _4.TourismContest.crawler.domain.Baseball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseballRepository extends JpaRepository<Baseball,Long> {
//    Optional<List<Baseball>> findAllByMonthAndDate(int month, int date);

    @Query("SELECT b FROM Baseball b WHERE b.month = :month AND b.date = :date AND b.time = :time AND b.home = :home AND b.away = :away AND b.location = :location")
    Optional<Baseball> findByMonthAndDateAndTimeAndAwayAndHomeAndLocation(@Param("month") int month, @Param("date") int date, @Param("time") String time, @Param("home") String home, @Param("away") String away, @Param("location") String location);
}
