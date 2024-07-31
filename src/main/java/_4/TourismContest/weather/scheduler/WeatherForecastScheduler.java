package _4.TourismContest.weather.scheduler;

import _4.TourismContest.weather.application.WeatherForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Component
public class WeatherForecastScheduler {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Scheduled(cron = "10 10 2,5,8,11,14,17,20,23 * * ?")
    public void fetchForecastData() throws IOException {

        int[][] stadium = new int[13][2];

        // 각 야구장의 좌표 추가
//        stadium[0] = new int[]{Math.round(37.5156f), Math.round(127.0730f)}; // 잠실야구장
//        stadium[1] = new int[]{Math.round(37.2859f), Math.round(127.0464f)}; // 수원 케이티 위즈 파크
//        stadium[2] = new int[]{Math.round(37.4352f), Math.round(126.6857f)}; // 인천 SSG 랜더스 필드
//        stadium[3] = new int[]{Math.round(35.2375f), Math.round(128.6811f)}; // 창원 NC파크
//        stadium[4] = new int[]{Math.round(35.1796f), Math.round(126.8786f)}; // 광주-기아 챔피언스필드
//        stadium[5] = new int[]{Math.round(35.1944f), Math.round(129.0617f)}; // 사직야구장
//        stadium[6] = new int[]{Math.round(35.8413f), Math.round(128.6812f)}; // 대구 삼성 라이온즈 파크
//        stadium[7] = new int[]{Math.round(36.3174f), Math.round(127.4278f)}; // 대전 한화생명 이글스파크
//        stadium[8] = new int[]{Math.round(37.4971f), Math.round(126.8679f)}; // 고척 스카이돔
//        stadium[9] = new int[]{Math.round(35.9670f), Math.round(126.7184f)}; // 월명종합경기장 야구장
//        stadium[10] = new int[]{Math.round(35.5422f), Math.round(129.2560f)}; // 울산 문수 야구장
//        stadium[11] = new int[]{Math.round(36.0190f), Math.round(129.3715f)}; // 포항 야구장
//        stadium[12] = new int[]{Math.round(36.6431f), Math.round(127.4917f)}; // 청주 야구장

        stadium[0] = new int[]{62,125}; // 잠실야구장
        stadium[1] = new int[]{61,121}; // 수원 케이티 위즈 파크
        stadium[2] = new int[]{55,124}; // 인천 SSG 랜더스 필드
        stadium[3] = new int[]{89,77}; // 창원 NC파크
        stadium[4] = new int[]{59,74}; // 광주-기아 챔피언스필드
        stadium[5] = new int[]{98,76}; // 사직야구장
        stadium[6] = new int[]{89,90}; // 대구 삼성 라이온즈 파크
        stadium[7] = new int[]{68,100}; // 대전 한화생명 이글스파크
        stadium[8] = new int[]{58,125}; // 고척 스카이돔
        stadium[9] = new int[]{56,92}; // 월명종합경기장 야구장
        stadium[10] = new int[]{101,84}; // 울산 문수 야구장
        stadium[11] = new int[]{102,94}; // 포항 야구장
        stadium[12] = new int[]{69,107}; // 청주 야구장

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH00");

        String baseDate = now.format(dateFormatter);
        String baseTime = now.format(timeFormatter);

        System.out.println("baseTime = " + baseTime);

        for (int[] point : stadium) {
            int nx = point[0]; // 경기장 X 좌표
            int ny = point[1]; // 경기장 Y 좌표
            weatherForecastService.fetchAndSaveForecastData(baseDate, baseTime, nx, ny);
        }
    }
}