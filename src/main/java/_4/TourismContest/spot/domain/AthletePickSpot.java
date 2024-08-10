package _4.TourismContest.spot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "athlete_pick_spot")
public class AthletePickSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String athleteName;
    private String phoneNumber;
    @Column(length = 1024)
    private String businessHour;
    private String closedDays;
    private String parkingFacilities;
    @Column(length = 256)
    private String address;
    @Column(length = 2500)
    private String introduce;
    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

}
