package _4.TourismContest.festival.repository;

import _4.TourismContest.festival.domain.FestivalImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FestivalImageRepository  extends JpaRepository<FestivalImage, Long> {
}
