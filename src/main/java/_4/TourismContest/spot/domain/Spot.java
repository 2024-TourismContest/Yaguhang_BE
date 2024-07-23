package _4.TourismContest.spot.domain;

import _4.TourismContest.spot.domain.Enum.FamilyFriendlyEnum;
import _4.TourismContest.spot.domain.Enum.LevelEnum;
import _4.TourismContest.spot.domain.Enum.SeasonEnum;
import _4.TourismContest.spot.domain.Enum.TypeEnum;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spot")
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "content_type_id", nullable = false)
    private Integer contenttypeid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tel")
    private String tel;

    @Column(name = "homepage")
    private String homepage;

    @Column(name = "mainimage")
    private String mainImage;

    @Column(name = "address")
    private String address;

    @Column(name = "mapx" , nullable = false)
    private Double mapx;

    @Column(name = "mapy", nullable = false)
    private Double mapy;

    @Column(name = "overview")
    private String overview;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SpotImage> SpotImages;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_category")
    private TypeEnum typeCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "leve_category")
    private LevelEnum leveCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "family_friendly_category")
    private FamilyFriendlyEnum familyFriendlyCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "season_category")
    private SeasonEnum seasonCategory;

    @Column(name = "active_type_category")
    private String activeTypeCategory;

}
