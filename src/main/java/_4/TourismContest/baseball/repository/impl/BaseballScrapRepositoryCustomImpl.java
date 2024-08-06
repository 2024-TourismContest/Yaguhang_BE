package _4.TourismContest.baseball.repository.impl;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.domain.QBaseball;
import _4.TourismContest.baseball.domain.QBaseballScrap;
import _4.TourismContest.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Optional;

public class BaseballScrapRepositoryCustomImpl implements BaseballScrapRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BaseballScrapRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Baseball> findUpcomingBaseballByUser(User user) {
        QBaseballScrap qBaseballScrap = QBaseballScrap.baseballScrap;
        QBaseball qBaseball = QBaseball.baseball;

        Baseball result = queryFactory.select(qBaseball)
                .from(qBaseballScrap)
                .join(qBaseballScrap.baseball, qBaseball)
                .where(qBaseballScrap.user.eq(user)
                        .and(qBaseball.time.after(LocalDateTime.now())))
                .orderBy(qBaseball.time.asc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}