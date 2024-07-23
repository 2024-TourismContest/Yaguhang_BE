package _4.TourismContest.crawler.repository;

import _4.TourismContest.crawler.domain.Baseball;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseballRepository extends JpaRepository<Baseball,Long> {
    Optional<List<Baseball>> findAllByMonthAndDate(int month, int date);
    Optional<Baseball> findByMonthAndDateAndTimeAndAwayAndHomeAndLocation(int month, int date, String time,String home, String away, String location);
}
