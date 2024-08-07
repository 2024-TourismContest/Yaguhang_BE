package _4.TourismContest.spot.domain;

import _4.TourismContest.stadium.domain.Stadium;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "spot")
public class Spot {
    @Id
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stadium stadium;
    private String name;
    private String image;
    private double mapX;    //X좌표
    private double mapY;    //Y좌표

    @Builder
    public Spot(Long contentId, Stadium stadium, String name, String image, double mapX, double mapY) {
        this.contentId = contentId;
        this.stadium = stadium;
        this.name = name;
        this.image = image;
        this.mapX = mapX;
        this.mapY = mapY;
    }
}
