package _4.TourismContest.stadium.application;

import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.dto.StadiumMapXY;
import _4.TourismContest.stadium.repository.StadiumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {
    private final StadiumRepository stadiumRepository;
    @Transactional
    public void saveStadiums(List<Stadium> stadiums) {
        if (stadiumRepository.count() == 0) {
            stadiumRepository.saveAll(stadiums);
        }
    }

    public StadiumMapXY getStadiumMapXY(String stadiumName) {
        Stadium stadium = stadiumRepository.findTopByName(stadiumName)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 구장 이름입니다."));

        return StadiumMapXY.builder()
                .stadiumId(stadium.getId())
                .name(stadiumName)
                .mapX(stadium.getX())
                .mapY(stadium.getY())
                .build();
    }
}
