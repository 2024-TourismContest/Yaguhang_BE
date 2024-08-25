package _4.TourismContest.recommend.domain;

import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend")
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stadium stadium;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private User user;

    @Column(name = "title")
    private String title;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "image")
    private String image;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Recommend (Stadium stadium, User user, String title){
        this.stadium = stadium;
        this.user = user;
        this.title = title;
    }
}
