package _4.TourismContest.review.domain;

import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    @Column(name = "like_count")
    private int likeCount;

    @CreatedDate
    private LocalDateTime createAt;

    @Builder
    public Review(User user, Spot spot, float star, String content) {
        this.id = null;
        this.user = user;
        this.spot = spot;
        this.star = star;
        this.content = content;
        this.likeCount = 0;
        this.createAt = null;
    }
}
