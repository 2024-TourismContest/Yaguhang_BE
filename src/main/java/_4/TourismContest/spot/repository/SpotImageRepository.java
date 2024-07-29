package _4.TourismContest.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotImageRepository extends JpaRepository<SpotImage, Long> {
}
