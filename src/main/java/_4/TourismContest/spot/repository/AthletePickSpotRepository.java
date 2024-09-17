package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.AthletePickSpot;
import _4.TourismContest.spot.domain.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface AthletePickSpotRepository extends JpaRepository<AthletePickSpot, Long> {
    List<AthletePickSpot> findAthletePickSpotsBySpotIn(List<Spot> spots);
    Optional<AthletePickSpot> findAthletePickSpotBySpot(Spot spot);
}
