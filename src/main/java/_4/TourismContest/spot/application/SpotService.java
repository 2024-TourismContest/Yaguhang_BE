package _4.TourismContest.spot.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.tour.infrastructure.TourApi;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SpotService {
    private final SpotRepository spotRepository;
    private final StadiumRepository stadiumRepository;
    private final TourApi tourApi;

    public SpotCategoryResponse getMainSpot(String stadium, String category){
        int radius = 10000; // 10km로 고정
        return tourApi.getMainSpot(getCoordinate(stadium), radius, category);
    }
    
    public MapXY getCoordinate(String stadiumName){
        Stadium stadium = stadiumRepository.findByName(stadiumName)
                .orElseThrow(() -> new BadRequestException("경기장 이름을 다시 확인해주세요"));
        return new MapXY(stadium.getX(), stadium.getY());
    }


}
