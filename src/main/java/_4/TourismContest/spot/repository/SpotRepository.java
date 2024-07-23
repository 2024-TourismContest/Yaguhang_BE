package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {
}
