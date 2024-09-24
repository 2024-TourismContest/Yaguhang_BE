package _4.TourismContest.review.repository;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findBySpot(Spot spot);
    List<Review> findAllBySpot(Spot spot);
    List<Review> findAllBySpotOrderByIdAsc(Spot spot);
    List<Review> findAllBySpotOrderByIdDesc(Spot spot);
    List<Review> findAllBySpotOrderByLikeCountDesc(Spot spot);
    List<Review> findAllByUserOrderByCreatedAtDesc(User user);
}
