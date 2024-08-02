package _4.TourismContest.stadium.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private Long contentId;
    private float x;
    private float y;
    private String name;
    private String team;
    private String image;
    private int nx;
    private int ny;
}
