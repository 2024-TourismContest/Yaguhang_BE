package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.SpotScrap;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpotScrapRepository extends JpaRepository <SpotScrap,Long>{
    @Query("SELECT ss FROM SpotScrap ss WHERE ss.spot.contentId=:contentid AND ss.user=:user")
    Optional<SpotScrap> findByUserAndContentId(User user, @Param("contentid")Long contentid);
}
