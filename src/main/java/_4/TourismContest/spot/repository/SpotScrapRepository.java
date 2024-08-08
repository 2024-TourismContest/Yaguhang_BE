package _4.TourismContest.spot.repository;

import _4.TourismContest.spot.domain.SpotScrap;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotScrapRepository extends JpaRepository <SpotScrap,Long>{
    @Query("SELECT ss FROM SpotScrap ss WHERE ss.spot.id=:contentid AND ss.user.id=:userid")
    Optional<SpotScrap> findByUserIdAndSpotContentId( @Param("userid")Long userid, @Param("contentid")Long contentid);

    @Query("SELECT ss FROM SpotScrap ss WHERE ss.user.id=:userid AND ss.spot.stadium.name=:name")
    List<SpotScrap> findByUserIdAndName(@Param("userid")Long userid, @Param("name")String name);

    Optional<List<SpotScrap>> findAllByUser(User user);
}
