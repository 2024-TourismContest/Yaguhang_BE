package _4.TourismContest.near.domain;

import _4.TourismContest.spot.domain.SpotScrap;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "near_scrap")
public class NearScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "near_scrap_id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private SpotScrap spotScrap;

    @ManyToOne(fetch = FetchType.LAZY)
    private Near near;
}
