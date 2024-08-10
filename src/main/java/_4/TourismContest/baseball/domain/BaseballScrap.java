package _4.TourismContest.baseball.domain;

import _4.TourismContest.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "baseball_scrap")
public class BaseballScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Baseball baseball;

    @Builder
    public BaseballScrap(User user, Baseball baseball) {
        this.user = user;
        this.baseball = baseball;
    }
}
