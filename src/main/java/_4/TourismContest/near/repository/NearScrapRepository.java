package _4.TourismContest.near.repository;

import _4.TourismContest.near.domain.NearScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NearScrapRepository extends JpaRepository<NearScrap, Long> {
}
