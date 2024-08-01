package _4.TourismContest.spot.application;

import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.repository.SpotRepository;
import _4.TourismContest.tour.infrastructure.TourApi;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SpotService {
    private final SpotRepository spotRepository;
    private final TourApi tourApi;

    public SpotCategoryResponse getMainSpot(String stadium, String category){
        int radius = 10000; // 10km로 고정
        return tourApi.getMainSpot(getCoordinate(stadium), radius, category);
    }

    public MapXY getCoordinate(String stadium){
        MapXY mapXY = new MapXY(129.061794, 35.1936215);    // TODO:각 구장 좌표값 가져오는 로직 필요
        
        return mapXY;
    }


}
