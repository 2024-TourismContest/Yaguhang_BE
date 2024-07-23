package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.SpotScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotScrapRepository extends JpaRepository <SpotScrap,Long>{
}
