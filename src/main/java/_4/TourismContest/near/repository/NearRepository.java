package _4.TourismContest.near.repository;

import _4.TourismContest.near.domain.Near;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NearRepository extends JpaRepository<Near, Long> {
}
