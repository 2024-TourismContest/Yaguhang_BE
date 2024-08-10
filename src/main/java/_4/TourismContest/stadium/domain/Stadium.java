package _4.TourismContest.stadium.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stadium")
public class Stadium {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float x;
    private float y;
    private String name;
    private String team;
    private String image;
    private int nx;
    private int ny;

    @Builder
    public Stadium(float x, float y, String name, String team, String image, int nx, int ny) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.team = team;
        this.image = image;
        this.nx = nx;
        this.ny = ny;
    }
}
