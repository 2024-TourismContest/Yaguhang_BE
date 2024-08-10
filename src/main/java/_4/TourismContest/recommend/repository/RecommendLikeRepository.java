package _4.TourismContest.recommend.repository;

import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.recommend.domain.RecommendLike;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendLikeRepository extends JpaRepository<RecommendLike, Long> {
    Optional<RecommendLike>findRecommendLikeByUserAndRecommend(User user, Recommend recommend);
}
