package _4.TourismContest.stadium.application;

import _4.TourismContest.stadium.domain.Stadium;
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
}
