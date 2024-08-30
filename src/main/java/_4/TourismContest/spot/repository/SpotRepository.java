package _4.TourismContest.spot.repository;

import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.domain.SpotCategory;
import _4.TourismContest.stadium.domain.Stadium;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

    List<Spot> findSpotsByStadiumAndCategory(Stadium stadium, SpotCategory category);
    List<Spot> findSpotsByStadiumAndCategory(Stadium stadium, SpotCategory category, Pageable pageable);
}
