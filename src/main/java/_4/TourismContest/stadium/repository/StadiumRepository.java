package _4.TourismContest.stadium.repository;

import _4.TourismContest.stadium.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
//    Optional<Stadium> findByName(String name);
    Optional<Stadium> findTopByName(String name);
    Optional<Stadium> findById(Long id);
}
