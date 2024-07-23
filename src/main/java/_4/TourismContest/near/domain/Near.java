package _4.TourismContest.near.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "near")
public class Near {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "near_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "content_type_id", nullable = false)
    private Integer contenttypeid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image")
    private String image;

    @Column(name = "address")
    private String address;

    @Column(name = "mapx" , nullable = false)
    private Double mapx;

    @Column(name = "mapy" , nullable = false)
    private Double mapy;

    @Column(name = "overview")
    private String overview;
}
