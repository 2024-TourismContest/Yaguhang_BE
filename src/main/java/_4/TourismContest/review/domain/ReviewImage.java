package _4.TourismContest.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_image")
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id", updatable = false, nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(name = "image", nullable = false)
    private String imageUrl;

    @Builder
    public ReviewImage(Long id, Review review, String imageUrl) {
        this.id = id;
        this.review = review;
        this.imageUrl = imageUrl;
    }

    @Builder
    public ReviewImage(Review review, String imageUrl) {
        this.review = review;
        this.imageUrl = imageUrl;
    }
}
