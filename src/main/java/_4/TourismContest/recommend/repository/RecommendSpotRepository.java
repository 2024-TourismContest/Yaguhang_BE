package _4.TourismContest.recommend.repository;

import _4.TourismContest.recommend.domain.RecommendSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendSpotRepository extends JpaRepository<RecommendSpot, Long> {
}
