package _4.TourismContest.festival.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "festival")
public class Festival {
    @Id
    @Column(name = "festival_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "content_type_id", nullable = false)
    private Integer contenttypeid;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tel")
    private String tel;

    @Column(name = "telname")
    private String telname;

    @Column(name = "homepage", length = 500)
    private String homepage;

    @Column(name = "firstimage1")
    private String firstimage1;

    @Column(name = "firstimage2")
    private String firstimage2;

    @Column(name = "address")
    private String address;

    @Column(columnDefinition = "TEXT", name = "overview", length = 500)  // 최대 길이 제한
    private String overview;

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FestivalImage> festivalImages;

    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FestivalScrap> festivalScrapList;
}