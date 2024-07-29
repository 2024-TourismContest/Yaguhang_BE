package _4.TourismContest.festival.application;

import _4.TourismContest.festival.domain.Festival;
import _4.TourismContest.festival.domain.FestivalImage;
import _4.TourismContest.festival.repository.FestivalImageRepository;
import _4.TourismContest.festival.repository.FestivalRepository;
import _4.TourismContest.festival.repository.FestivalScrapRepository;
import lombok.RequiredArgsConstructor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
public class FestivalService {
    private final FestivalRepository festivalRepository;
    private final FestivalImageRepository festivalImageRepository;
    private final FestivalScrapRepository festivalScrapRepository;
    private final RestTemplate restTemplate;

    @Value("${tour_api.secret.KorService1}")
    private String apiKey;

    /**
     * 페스티벌 업데이트 로직
     *
     * @param startDate
     * @param endDate
     */
    @Transactional
    public void updateFestivalList(String startDate, String endDate) {
        String festivalListUrl = String.format(
                "http://apis.data.go.kr/B551011/KorService1/searchFestival1?eventStartDate=%s&eventEndDate=%s&areaCode=&sigunguCode=&ServiceKey=%s&listYN=Y&_type=json&MobileOS=ETC&MobileApp=AppTest&arrange=A&numOfRows=100&pageNo=1",
                startDate, endDate, apiKey);
        String response = restTemplate.getForObject(festivalListUrl, String.class);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            JSONObject responseObj = (JSONObject) jsonResponse.get("response");
            JSONObject bodyObj = (JSONObject) responseObj.get("body");
            JSONObject itemsObj = (JSONObject) bodyObj.get("items");
            JSONArray festivalList = (JSONArray) itemsObj.get("item");

            for (Object festivalObj : festivalList) {
                JSONObject festivalJson = (JSONObject) festivalObj;
                String festivalId = String.valueOf(festivalJson.get("contentid"));
                fetchFestivalDetail(festivalId);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 축제 상세 정보
     *
     * @param contentId
     */
    @Transactional
    private void fetchFestivalDetail(String contentId) {
        String festivalDetailUrl = String.format(
                "http://apis.data.go.kr/B551011/KorService1/detailCommon1?ServiceKey=%s&contentTypeId=15&contentId=%s&MobileOS=ETC&MobileApp=AppTest&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&_type=json",
                apiKey, contentId);
        String festivalDetailResponse = restTemplate.getForObject(festivalDetailUrl, String.class);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonResponse = (JSONObject) parser.parse(festivalDetailResponse);
            JSONObject responseObj = (JSONObject) jsonResponse.get("response");
            JSONObject bodyObj = (JSONObject) responseObj.get("body");
            JSONObject itemsObj = (JSONObject) bodyObj.get("items");
            JSONArray itemArray = (JSONArray) itemsObj.get("item");

            if (itemArray != null && !itemArray.isEmpty()) {
                JSONObject itemObj = (JSONObject) itemArray.get(0);

                Long id = Long.parseLong(contentId);
                String contentTypeIdStr = String.valueOf(itemObj.get("contenttypeid"));
                Integer contentTypeId = Integer.parseInt(contentTypeIdStr);
                String title = String.valueOf(itemObj.getOrDefault("title", ""));
                String overview = String.valueOf(itemObj.getOrDefault("overview", ""));
                String addr1 = String.valueOf(itemObj.getOrDefault("addr1", ""));
                String addr2 = String.valueOf(itemObj.getOrDefault("addr2", ""));
                String firstImage1 = String.valueOf(itemObj.getOrDefault("firstimage", ""));
                String firstImage2 = String.valueOf(itemObj.getOrDefault("firstimage2", ""));
                String tel = String.valueOf(itemObj.getOrDefault("tel", ""));
                String telname = String.valueOf(itemObj.getOrDefault("telname", ""));
                String homepage = String.valueOf(itemObj.getOrDefault("homepage", ""));
                // URL 앞의 한글문자, 공백문자 제거
                String cleanedHomepage = homepage.replaceAll(".*?\\s*(<a.*?>)", "$1");
                String address = addr1 + (addr2 != null && !addr2.isEmpty() ? " " + addr2 : "");
                Festival festival = Festival.builder()
                        .id(id)
                        .contenttypeid(contentTypeId)
                        .title(title)
                        .tel(tel)
                        .telname(telname)
                        .homepage(cleanedHomepage)
                        .firstimage1(firstImage1)
                        .firstimage2(firstImage2)
                        .address(address)
                        .overview(overview)
                        .build();

                festivalRepository.save(festival);

                parseFestivalImages(contentId, festival);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * JSON 응답을 파싱하여 FestivalImage 객체 리스트로 변환
     *
     * @param contentId
     * @param festival
     * @return
     */
    @Transactional
    private void parseFestivalImages(String contentId, Festival festival) {
        String detailImageUrl = String.format(
                "http://apis.data.go.kr/B551011/KorService1/detailImage1?ServiceKey=%s&contentId=%s&MobileOS=ETC&MobileApp=AppTest&imageYN=Y&subImageYN=Y&numOfRows=100&_type=json",
                apiKey, contentId);

        String response = restTemplate.getForObject(detailImageUrl, String.class);

        List<FestivalImage> festivalImages = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            JSONObject responseObj = (JSONObject) jsonResponse.get("response");
            JSONObject bodyObj = (JSONObject) responseObj.get("body");
            String itemsStr = String.valueOf(bodyObj.get("items"));
            if (itemsStr.isEmpty() || itemsStr.equals("null")) {
                System.out.println("No items found in response");
                return; // 또는 적절한 예외 처리
            }
            JSONObject itemsObj = (JSONObject) bodyObj.get("items");

            JSONArray itemArray = (JSONArray) itemsObj.get("item");
            if (itemArray != null) {
                for (Object itemObj : itemArray) {
                    JSONObject item = (JSONObject) itemObj;

                    try {
                        String imageUrl = String.valueOf(item.get("originimgurl"));

                        FestivalImage festivalImage = FestivalImage.builder()
                                .id(Long.parseLong(contentId))
                                .festival(festival)
                                .imageUrl(imageUrl)
                                .build();

                        festivalImages.add(festivalImage);
                    } catch (Exception e) {
                        String originImgUrl = String.valueOf(item.get("originimgurl"));
                        System.out.println("originImgUrl = " + originImgUrl);
                        e.printStackTrace();
                    }
                }
            }
        } catch (ParseException e) {
            System.out.println("response = " + response);
            e.printStackTrace();
        }

        festivalImageRepository.saveAll(festivalImages);
    }
}