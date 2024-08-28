package _4.TourismContest.spot.dto.spotDetailResponse;

import _4.TourismContest.spot.domain.AthletePickSpot;
import _4.TourismContest.spot.domain.Spot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SpotAthletePickDetailResponse(
        Long contentId,
        String name,
        String address,
        Boolean isScraped,
        String phoneNumber,
        String description,
        String picker,
        String buisnessHour,
        String closedDays,
        String parkingFacilities,
        List<String> images
) implements SpotDetailResponse {
    public static SpotAthletePickDetailResponse makeSpotAthletePickDetailResponse(Spot spot,
                                                                                  AthletePickSpot athletePickSpot,
                                                                                  Boolean isScraped) {
        return new SpotAthletePickDetailResponse(
                spot.getId(),
                spot.getName(),
                spot.getAddress(),
                isScraped,
                athletePickSpot.getPhoneNumber(),
                athletePickSpot.getIntroduce(),
                athletePickSpot.getAthleteName(),
                athletePickSpot.getBusinessHour(),
                athletePickSpot.getClosedDays(),
                athletePickSpot.getParkingFacilities(),
                new ArrayList<>(Arrays.asList(spot.getImage()))
        );
    }
}
