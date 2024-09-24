package _4.TourismContest.tour.infrastructure;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.spot.dto.MapXY;
import _4.TourismContest.tour.dto.Enum.ContentType;
import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.dto.TourApiDetailImageResponseDto;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import _4.TourismContest.tour.dto.detailIntroResponse.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

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

    public TourApiResponseDto getSpot(float x, float y, int radius, int contentTypeId, int pageSize) throws IOException { // 좌표 값 주변 리스트 가져오는 메소드
        try{
            String ENDPOINT = "/locationBasedList1";
            String url = TOUR_API_BASE_URL + ENDPOINT;

            int randomPageMax = getTotalCount(x,y,radius,contentTypeId) / pageSize;

            Random random = new Random();
            int randomPage = 0;
            if(randomPageMax > 1){
                randomPage = random.nextInt(randomPageMax-1);
            }

            URI uri = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("serviceKey", URLEncoder.encode(korService1_secret, StandardCharsets.UTF_8))
                    .queryParam("numOfRows" , pageSize)
                    .queryParam("pageNo",randomPage)
                    .queryParam("MobileOS", URLEncoder.encode("ETC", StandardCharsets.UTF_8))
                    .queryParam("MobileApp", URLEncoder.encode("yaguhang", StandardCharsets.UTF_8))
                    .queryParam("mapX", URLEncoder.encode(String.valueOf(x), StandardCharsets.UTF_8))
                    .queryParam("mapY", URLEncoder.encode(String.valueOf(y), StandardCharsets.UTF_8))
                    .queryParam("radius", URLEncoder.encode(String.valueOf(radius), StandardCharsets.UTF_8))
                    .queryParam("contentTypeId", URLEncoder.encode(String.valueOf(contentTypeId), StandardCharsets.UTF_8))
                    .queryParam("_type", URLEncoder.encode("json", StandardCharsets.UTF_8))
                    .build(true)
                    .toUri();

//        String responseEntity = restTemplate.getForObject(uri, String.class);
//        TourApiResponseDto tourApiResponseDto = parseResponse(responseEntity);   // xml -> json으로 파싱이 필요한 경우
            TourApiResponseDto tourApiResponseDto = restTemplate.getForObject(uri, TourApiResponseDto.class);

            return tourApiResponseDto;
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    public Integer getTotalCount (float x, float y, int radius, int contentTypeId) throws IOException{ // total 개수 가져오는 메소드
        String ENDPOINT = "/locationBasedList1";
        String url = TOUR_API_BASE_URL + ENDPOINT;

        URI getPageuri = UriComponentsBuilder.fromHttpUrl(url)
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
        //
        TourApiResponseDto tempTARD = restTemplate.getForObject(getPageuri, TourApiResponseDto.class);
        return tempTARD.getResponse().getBody().getTotalCount();
    }

    public TourApiDetailCommonResponseDto getSpotDetailCommon(Long contentId) {  // 공통 정보 가져오는 메소드

        String ENDPOINT = "/detailCommon1";
        String url = TOUR_API_BASE_URL + ENDPOINT;


        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("serviceKey", URLEncoder.encode(korService1_secret, StandardCharsets.UTF_8))
                .queryParam("MobileOS", URLEncoder.encode("ETC", StandardCharsets.UTF_8))
                .queryParam("MobileApp", URLEncoder.encode("yaguhang", StandardCharsets.UTF_8))
                .queryParam("contentId", URLEncoder.encode(String.valueOf(contentId), StandardCharsets.UTF_8))
                .queryParam("_type", URLEncoder.encode("json", StandardCharsets.UTF_8))
                .queryParam("overviewYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .queryParam("mapinfoYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .queryParam("addrinfoYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .queryParam("firstImageYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .queryParam("defaultYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .build(true)
                .toUri();


        TourApiDetailCommonResponseDto tourApiDetailCommonResponseDto = restTemplate.getForObject(uri, TourApiDetailCommonResponseDto.class);

        return tourApiDetailCommonResponseDto;
    }

    public TourApiDetailImageResponseDto getSpotDetailImage(Long contentId) { // 추가 이미지 정보 가져오는 메소드

        String ENDPOINT = "/detailImage1";
        String url = TOUR_API_BASE_URL + ENDPOINT;


        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("serviceKey", URLEncoder.encode(korService1_secret, StandardCharsets.UTF_8))
                .queryParam("MobileOS", URLEncoder.encode("ETC", StandardCharsets.UTF_8))
                .queryParam("MobileApp", URLEncoder.encode("yaguhang", StandardCharsets.UTF_8))
                .queryParam("contentId", URLEncoder.encode(String.valueOf(contentId), StandardCharsets.UTF_8))
                .queryParam("_type", URLEncoder.encode("json", StandardCharsets.UTF_8))
                .queryParam("imageYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .queryParam("subImageYN", URLEncoder.encode("Y", StandardCharsets.UTF_8))
                .build(true)
                .toUri();



        TourApiDetailImageResponseDto tourApiDetailImageResponseDto = restTemplate.getForObject(uri, TourApiDetailImageResponseDto.class);

        return tourApiDetailImageResponseDto;
    }

    public TourApiDetailIntroResponseDto getSpotDetailIntro(Long contentId, String category) { // 소개 정보 가져오는 메소드
        String contentTypeId = String.valueOf(getContentTypeId(category));
        URI uri = getIntroUri(contentId, contentTypeId);
        TourApiDetailIntroResponseDto tourApiDetailIntroResponseDto;

        if(category.equals("숙소")){
            tourApiDetailIntroResponseDto = restTemplate.getForObject(uri, TourApiAccommodationDetailIntroResponseDto.class);
        }
        else if(category.equals("맛집")){
            tourApiDetailIntroResponseDto = restTemplate.getForObject(uri, TourApiRestaurantDetailIntroResponseDto.class);
        }
        else if(category.equals("문화")){
            tourApiDetailIntroResponseDto = restTemplate.getForObject(uri, TourApiCultureDetailIntroResponseDto.class);
        }
        else {
            tourApiDetailIntroResponseDto = restTemplate.getForObject(uri, TourApiShoppingDetailIntroResponseDto.class);
        }

        return tourApiDetailIntroResponseDto;
    }

    public URI getIntroUri(Long contentId, String contentTypeId){ // 카데고리에 맞는 api uri 생성 메소드
        String ENDPOINT = "/detailIntro1";
        String url = TOUR_API_BASE_URL + ENDPOINT;

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("serviceKey", URLEncoder.encode(korService1_secret, StandardCharsets.UTF_8))
                .queryParam("MobileOS", URLEncoder.encode("ETC", StandardCharsets.UTF_8))
                .queryParam("MobileApp", URLEncoder.encode("yaguhang", StandardCharsets.UTF_8))
                .queryParam("contentId", URLEncoder.encode(String.valueOf(contentId), StandardCharsets.UTF_8))
                .queryParam("_type", URLEncoder.encode("json", StandardCharsets.UTF_8))
                .queryParam("contentTypeId", URLEncoder.encode(contentTypeId, StandardCharsets.UTF_8))
                .build(true)
                .toUri();
        return uri;
    }


    public TourApiResponseDto getMainSpot(MapXY mapXY, int radius, String category){
        try {
            return getSpot(mapXY.x(), mapXY.y(), radius, getContentTypeId(category), 4);
        } catch (IOException e) {
            return null;
        }
    }

    public TourApiResponseDto getStadiumSpot(MapXY mapXY, int radius, String category, int pageSize){
        try {
            return getSpot(mapXY.x(), mapXY.y(), radius, getContentTypeId(category), pageSize);
        } catch (IOException e) {
            return null;
        }
    }

    public int getContentTypeId(String category){ // 카테고리 값으로 contentTypeId 가져오기
        if(category.equals("문화"))
            return ContentType.문화.getValue();
        if(category.equals("숙소"))
            return ContentType.숙소.getValue();
        if(category.equals("쇼핑"))
            return ContentType.쇼핑.getValue();
        if(category.equals("맛집"))
            return ContentType.맛집.getValue();

        throw new BadRequestException("category 값을 확인해주세요");
    }
}

