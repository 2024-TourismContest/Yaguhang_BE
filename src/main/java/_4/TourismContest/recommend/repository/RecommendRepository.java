package _4.TourismContest.recommend.repository;

import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendRepository extends JpaRepository<Recommend, Long> {
//    @Query("SELECT r FROM Recommend r WHERE r.stadium.name=:stadiumName")
//    List<Recommend> findByLikes(@Param("stadiumName")String stadiumName, Pageable pageable);

    @Query("SELECT r FROM Recommend r")
    Page<Recommend> findRecommendList(Pageable pageable);

    @Query("SELECT r FROM Recommend r where r.stadium.name=:filter")
    Page<Recommend> findRecommendListByfilter(Pageable pageable, @Param("filter")String filter);

    @Query("SELECT r FROM Recommend r WHERE r.user.id=:userId")
    List<Recommend> findRecommendByUser(@Param("userId")Long userId, Pageable pageable);

    @Query("SELECT r from Recommend  r where r.stadium.name=:filter and r.title like :keyWord")
    Page<Recommend> findRecommendsByFilterAndKeyWord(Pageable pageable, @Param("filter") String filter, @Param("keyWord") String keyWord);

    @Query("SELECT r from Recommend  r where r.title like :keyWord")
    Page<Recommend> findRecommendsByKeyWord(Pageable pageable, @Param("keyWord") String keyWord);

    List<Recommend> findAllByUser(User user);
}
