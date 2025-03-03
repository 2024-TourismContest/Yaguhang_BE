package _4.TourismContest.stadium.repository;

import _4.TourismContest.stadium.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
    @Query("SELECT s FROM Stadium s WHERE s.name LIKE %:name%")
    Optional<Stadium> findTopByNameContaining(@Param("name") String name);



    Optional<Stadium> findTopById(Long id);
}
