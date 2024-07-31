package _4.TourismContest.tour.infrastructure;

import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import static _4.TourismContest.spot.dto.event.SpotCategoryResponse.tourApiToSpotCategoryResponse;

@Service
public class TourApi {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    TourApi(RestTemplate restTemplate, ObjectMapper objectMapper){
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    private static final String TOUR_API_BASE_URL = "http://apis.data.go.kr/B551011/KorService1";
    @Value("${tour_api.secret.KorService1}")
    private String korService1_secret;
    public TourApiResponseDto getSpot(double x, double y, int radius, int contentTypeId) throws IOException {

        String ENDPOINT = "/locationBasedList1";
        String url = TOUR_API_BASE_URL + ENDPOINT;

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("serviceKey", URLEncoder.encode(korService1_secret, StandardCharsets.UTF_8))
                .queryParam("MobileOS", URLEncoder.encode("ETC", StandardCharsets.UTF_8))
                .queryParam("MobileApp", URLEncoder.encode("yaguhang", StandardCharsets.UTF_8))
                .queryParam("mapX", URLEncoder.encode(String.valueOf(x), StandardCharsets.UTF_8))
                .queryParam("mapY", URLEncoder.encode(String.valueOf(y), StandardCharsets.UTF_8))
                .queryParam("radius", URLEncoder.encode(String.valueOf(radius), StandardCharsets.UTF_8))
                .queryParam("contentTypeId", URLEncoder.encode(String.valueOf(contentTypeId), StandardCharsets.UTF_8))
                .queryParam("_type", URLEncoder.encode("json", StandardCharsets.UTF_8))
                .build(true)
                .toUri();

        String responseEntity = restTemplate.getForObject(uri, String.class);

        TourApiResponseDto tourApiResponseDto = parseResponse(responseEntity);
        return tourApiResponseDto;
    }


    public TourApiResponseDto parseResponse(String responseString) throws IOException {

        return objectMapper.readValue(responseString, TourApiResponseDto.class);
    }

    public SpotCategoryResponse getMainSpot(MapXY mapXY, int radius, String category){
        int contentTypeId = 39;   // TODO: category로 contentTypeId 값 가져오는 로직
        try {
            // responseString이 JSON 형식이어야 합니다.
            TourApiResponseDto tourApiResponseDto = getSpot(mapXY.x(), mapXY.y(), radius, contentTypeId);
            return tourApiToSpotCategoryResponse(tourApiResponseDto, category);
        } catch (IOException e) {
            // 예외가 발생한 경우 처리할 로직
            e.printStackTrace(); // 예외의 스택 트레이스를 출력
            return null; // 오류가 발생하면 null을 반환하거나 다른 대체 로직
        }
    }
}

