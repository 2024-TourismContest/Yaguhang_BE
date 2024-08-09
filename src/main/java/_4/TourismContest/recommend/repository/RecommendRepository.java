package _4.TourismContest.recommend.repository;

import _4.TourismContest.recommend.domain.Recommend;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query("SELECT r FROM Recommend r WHERE r.stadium.id=:stadiumId ORDER BY r.likeCount DESC")
    List<Recommend> findByLikes(@Param("stadiumId")Long stadiumId, Pageable pageable);

}
