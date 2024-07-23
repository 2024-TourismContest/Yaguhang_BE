package _4.TourismContest.festival.repository;

import _4.TourismContest.festival.domain.FestivalScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivalScrapRepository extends JpaRepository<FestivalScrap, Long> {
}
