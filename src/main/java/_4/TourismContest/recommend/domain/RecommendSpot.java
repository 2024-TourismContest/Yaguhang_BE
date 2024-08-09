package _4.TourismContest.recommend.domain;

import _4.TourismContest.spot.domain.Spot;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend_spot")
public class RecommendSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recommend recommend;
}
