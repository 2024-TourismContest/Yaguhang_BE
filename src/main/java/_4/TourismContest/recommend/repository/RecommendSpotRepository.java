package _4.TourismContest.recommend.repository;

import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.recommend.domain.RecommendSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendSpotRepository extends JpaRepository<RecommendSpot, Long> {
    List<RecommendSpot> findByRecommend(Recommend recommend);
    void deleteAllByRecommend(Recommend recommend);
}
