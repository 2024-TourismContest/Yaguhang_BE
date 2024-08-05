package _4.TourismContest.stadium.presentation;

import _4.TourismContest.stadium.application.StadiumService;
import _4.TourismContest.stadium.domain.Stadium;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class StadiumController {

    private final StadiumService stadiumService;

    @PostConstruct
    public void init() {
        List<Stadium> stadiums = Arrays.asList(
                Stadium.builder()
                        .y(37.5156f).x(127.0730f).name("잠실").team("LG 트윈스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/jamsilStadium.jpeg").nx(62).ny(125)
                        .build(),
                Stadium.builder()
                        .y(37.5156f).x(127.0730f).name("잠실").team("두산 베어스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/jamsilStadium.jpeg").nx(62).ny(125)
                        .build(),
                Stadium.builder()
                        .y(37.2859f).x(127.0464f).name("수원").team("KT 위즈")
                        .image("https://yaguhang.kro.kr:8443/stadiums/suwonStadium.jpeg").nx(61).ny(121)
                        .build(),
                Stadium.builder()
                        .y(37.4352f).x(126.6857f).name("문학").team("SSG 랜더스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/incheonStadium.jpeg").nx(55).ny(124)
                        .build(),
                Stadium.builder()
                        .y(35.2375f).x(128.6811f).name("창원").team("NC 다이노스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/changwonStadium.webp").nx(89).ny(77)
                        .build(),
                Stadium.builder()
                        .y(35.1796f).x(126.8786f).name("광주").team("KIA 타이거즈")
                        .image("https://yaguhang.kro.kr:8443/stadiums/kiaChampionsField.webp").nx(59).ny(74)
                        .build(),
                Stadium.builder()
                        .y(35.1944f).x(129.0617f).name("사직").team("롯데 자이언츠")
                        .image("https://yaguhang.kro.kr:8443/stadiums/sajikStadium.webp").nx(98).ny(76)
                        .build(),
                Stadium.builder()
                        .y(35.8413f).x(128.6812f).name("대구").team("삼성 라이온즈")
                        .image("https://yaguhang.kro.kr:8443/stadiums/samsungLionsPark.webp").nx(89).ny(90)
                        .build(),
                Stadium.builder()
                        .y(36.3174f).x(127.4278f).name("대전").team("한화 이글스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/hanwhaEaglesPark.webp").nx(68).ny(100)
                        .build(),
                Stadium.builder()
                        .y(37.4971f).x(126.8679f).name("고척").team("키움 히어로즈")
                        .image("https://yaguhang.kro.kr:8443/stadiums/gocheokStadium.webp").nx(58).ny(125)
                        .build(),
                Stadium.builder()
                        .y(35.5422f).x(129.2560f).name("울산").team("롯데 자이언츠")
                        .image("https://yaguhang.kro.kr:8443/stadiums/ulsanMunsuStadium.webp").nx(101).ny(84)
                        .build(),
                Stadium.builder()
                        .y(36.0190f).x(129.3715f).name("포항").team("삼성 라이온즈")
                        .image("https://yaguhang.kro.kr:8443/stadiums/pohangStadium.webp").nx(102).ny(94)
                        .build(),
                Stadium.builder()
                        .y(36.6431f).x(127.4917f).name("청주").team("한화 이글스")
                        .image("https://yaguhang.kro.kr:8443/stadiums/cheongjuStadium.webp").nx(69).ny(107)
                        .build()
        );
        stadiumService.saveStadiums(stadiums);
    }
}