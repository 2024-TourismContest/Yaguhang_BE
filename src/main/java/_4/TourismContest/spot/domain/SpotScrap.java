package _4.TourismContest.spot.domain;

import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spot_scrap")
public class SpotScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

    @Builder
    public SpotScrap(User user, Spot spot) {
        this.user = user;
        this.spot = spot;
    }
}
