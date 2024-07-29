package _4.TourismContest.review.domain;

import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", updatable = false, nullable = false)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private float star;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "like_count")
    private int likeCount;

    @CreatedDate
    private LocalDateTime createAt;
}
