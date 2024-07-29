package _4.TourismContest.tour.infrastructure;

import _4.TourismContest.spot.dto.event.MapXY;
import _4.TourismContest.spot.dto.event.SpotBasicPreviewDto;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static _4.TourismContest.spot.dto.event.SpotCategoryResponse.tourApiToSpotCategoryResponse;

@Service
public class TourApi {
    private static final String TOUR_API_BASE_URL = "http://apis.data.go.kr/B551011/KorService1";
//    @Value("${tour_api.secret.KorService1}")
    private static final String korService1_secret = "HdI3AuEi9LTwWZOuy%2Fjg6AZG%2Bjn8tUxLHK01DzSA24nLvwRSqwrrq8kjLaagm7eARg%2Fg8MQ3CYplpJT3Nbipaw%3D%3D";
    public TourApiResponseDto getSpot(double x, double y, int radius, int contentTypeId) {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + user.getGithubToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        String ENDPOINT = "/locationBasedList1";
        String url = TOUR_API_BASE_URL + ENDPOINT;

        String serviceKey = "HdI3AuEi9LTwWZOuy%2Fjg6AZG%2Bjn8tUxLHK01DzSA24nLvwRSqwrrq8kjLaagm7eARg%2Fg8MQ3CYplpJT3Nbipaw%3D%3D";
        String finalUrl = null;
        try {
            // 다른 파라미터 인코딩
            String mobileOS = URLEncoder.encode("ETC", "UTF-8");
            String mobileApp = URLEncoder.encode("yaguhang", "UTF-8");
            String mapX = URLEncoder.encode(String.valueOf(x), "UTF-8");
            String mapY = URLEncoder.encode(String.valueOf(y), "UTF-8");
            String radiusEncoded = URLEncoder.encode(String.valueOf(radius), "UTF-8");
            String contentTypeIdEncoded = URLEncoder.encode(String.valueOf(contentTypeId), "UTF-8");
            String type = URLEncoder.encode("json", "UTF-8");

            // URL 구성
            finalUrl = String.format("%s?MobileOS=%s&MobileApp=%s&mapX=%s&mapY=%s&radius=%s&serviceKey=%s&contentTypeId=%s&_type=%s",
                    url, mobileOS, mobileApp, mapX, mapY, radiusEncoded, serviceKey, contentTypeIdEncoded, type);

            System.out.println(finalUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 쿼리 파라미터 설정
//        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
//                .queryParam("MobileOS", "ETC")
//                .queryParam("MobileApp", "yaguhang")
//                .queryParam("mapX", x)
//                .queryParam("mapY" , y)
//                .queryParam("radius", radius)
//                .queryParam("serviceKey", "HdI3AuEi9LTwWZOuy/jg6AZG+jn8tUxLHK01DzSA24nLvwRSqwrrq8kjLaagm7eARg/g8MQ3CYplpJT3Nbipaw==")
//                .queryParam("contentTypeId", contentTypeId)
//                .queryParam("_type", "json");

//        System.out.println(builder.toUriString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TourApiResponseDto> responseEntity = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TourApiResponseDto.class
        );

        TourApiResponseDto tourApiResponseDto = responseEntity.getBody();
        return tourApiResponseDto;
    }

    public SpotCategoryResponse getMainSpot(MapXY mapXY, int radius, String category){
        int contentTypeId = 39;   // TODO: contentTypeId 값 가져오는 로직

        TourApiResponseDto tourApiResponseDto = getSpot(mapXY.x(), mapXY.y(), radius, contentTypeId);
        return tourApiToSpotCategoryResponse(tourApiResponseDto, category);
    }
}

