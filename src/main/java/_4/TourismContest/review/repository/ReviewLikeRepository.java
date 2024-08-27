package _4.TourismContest.review.repository;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewLike;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByUserAndReview(User user, Review review);
}
