package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.AthletePickSpot;
import _4.TourismContest.spot.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AthletePickSpotRepository extends JpaRepository<AthletePickSpot, Long> {
    List<AthletePickSpot> findAthletePickSpotsBySpotIn(List<Spot> spots);
}
