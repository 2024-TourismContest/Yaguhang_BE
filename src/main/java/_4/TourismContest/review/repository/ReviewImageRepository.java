package _4.TourismContest.review.repository;

import _4.TourismContest.review.domain.Review;
import _4.TourismContest.review.domain.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findAllByReview(Review review);
}
