# ⚾ 야구보고 ✈️ 여행도 가고!  
### **야구팬들을 위한 특별한 웹서비스, 야구행**


![_mockup2](https://private-user-images.githubusercontent.com/48638700/371865993-ddfc324d-a044-4594-ac1b-efb61cb114f2.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mjc2MjQ1MzMsIm5iZiI6MTcyNzYyNDIzMywicGF0aCI6Ii80ODYzODcwMC8zNzE4NjU5OTMtZGRmYzMyNGQtYTA0NC00NTk0LWFjMWItZWZiNjFjYjExNGYyLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDA5MjklMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQwOTI5VDE1MzcxM1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTA2NWU2M2ZhMTc0MDJiMGEwMjE5ZjYzZWViMDE4YzRjNGY4Yzc4NDgyNjgzYjUyMWE5Y2ZjNGU1YjU2Y2Y3ODMmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0._7wLA5QbkPF8bhUSd5RgF-kQrpBeTx4RLZf99JII_h0)

- 배포 URL : https://yaguhang.kro.kr:7443
- Test ID : yaguhang@test.com
- Test PW : yaguhangTest!

<br>

## 프로젝트 소개

- 야구행은 야구 경기를 관람하고, 그 열기를 간직한 채 바로 여행을 떠날 수 있는 맞춤형 웹서비스입니다.
- 야구를 사랑하는 팬들을 위해, 경기를 즐긴 후 인근 지역에서의 여행 일정까지 한 번에 계획할 수 있도록 도와드립니다.
- 야구 경기 일정과 해당 경기의 날씨 정보를 확인할 수 있습니다.
- 야구 경기 스크랩을 통해 관심 있는 경기를 추후 쉽게 확인할 수 있습니다.
- TourAPI를 통해 야구 경기장 인근에서 선수들이 추천하는 맛집, 숙소, 음식, 문화, 쇼핑 명소를 안내해드립니다.
- 지도를 이용한 주변 관광지 탐색 기능 지도를 활용해 경기장 주변의 추천 명소(맛집, 숙소, 음식, 문화, 쇼핑)를 쉽게 탐색할 수 있습니다.
- 관광지에 대한 상세 정보(주소, 리뷰, 영업시간 등)를 제공합니다.
- 주변 관광지 리뷰, 좋아요, 스크랩 기능을 통해 관광지에 대한 리뷰를 남기고, 좋아요를 누르며, 마음에 드는 장소는 스크랩할 수 있습니다.
- 추천 여행지 목록을 제작하여 나만의 맞춤형 여행지를 다른 사용자들과 공유할 수 있습니다.

<br>

## ✨ 기술 스택 (Tech Stack)
<img src="https://img.shields.io/badge/java-EF5C55?style=for-the-badge"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 

<br/>


<br>

## 팀원 구성

<div align="center">

| **최민석** | **김종경** | **양두영** |
| :------: |  :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/120546936?v=4" height=150 width=150> <br/> @m0304s](https://github.com/m0304s) | [<img src="https://avatars.githubusercontent.com/u/111286262?v=4" height=150 width=150> <br/> @JONG_KYEONG](https://github.com/JONG-KYEONG) | [<img src="https://avatars.githubusercontent.com/u/48638700?v=4" height=150 width=150> <br/> @FhRh](https://github.com/FhRh) |

</div>


<br>

## 1. 개발 환경

- Front : HTML, React, TypeScript, Axios
- Back-end : Java, Spring Boot, MariaDB, Tour API 활용
- 버전 및 이슈관리 : Github, Github Issues, Github Project
- 협업 툴 : Discord, Notion
- 서비스 배포 환경 : 셀프호스팅
- 디자인 : [Figma](https://www.figma.com/file/fAisC2pEKzxTOzet9CfqML/README(oh-my-code)?node-id=39%3A1814)
<br>

<br>

## 3. 프로젝트 구조

```
└─TourismContest
    │  TourismContestApplication.java
    │  
    ├─aws
    │  ├─application
    │  │      AwsS3.java
    │  │      AwsUtils.java
    │  │      
    │  ├─config
    │  │      AwsS3Config.java
    │  │      
    │  └─presentation
    │          AwsController.java
    │          
    ├─baseball
    │  ├─application
    │  │      BaseballScrapService.java
    │  │      BaseballService.java
    │  │      
    │  ├─domain
    │  │      Baseball.java
    │  │      BaseballScrap.java
    │  │      
    │  ├─dto
    │  │      BaseBallDTO.java
    │  │      BaseballScheduleDTO.java
    │  │      BaseBallSchedulePerMonthDTO.java
    │  │      BaseBallScrapResponseDTO.java
    │  │      ScrappedBaseballDTO.java
    │  │      
    │  ├─presentation
    │  │      BaseballController.java
    │  │      BaseballScrapController.java
    │  │      
    │  ├─repository
    │  │  │  BaseballRepository.java
    │  │  │  BaseballScrapRepository.java
    │  │  │  
    │  │  └─impl
    │  │          BaseballScrapRepositoryCustom.java
    │  │          BaseballScrapRepositoryCustomImpl.java
    │  │          
    │  └─scheduler
    │          BaseballScheduler.java
    │          
    ├─common
    │      Response.java
    │      
    ├─config
    │      AppProperties.java
    │      CorsProperties.java
    │      QuerydslConfig.java
    │      RestTemplateConfig.java
    │      SecurityConfig.java
    │      SwaggerConfig.java
    │      WebConfig.java
    │      
    ├─Enum
    │      ResponseType.java
    │      
    ├─exception
    │      BadRequestException.java
    │      OAuth2AuthenticationProcessingException.java
    │      ResourceNotFoundException.java
    │      
    ├─oauth
    │  ├─application
    │  │  │  CurrentUser.java
    │  │  │  CustomUserDetailsService.java
    │  │  │  CustomUsernamePasswordAuthenticationToken.java
    │  │  │  RestAuthenticationEntryPoint.java
    │  │  │  TokenAuthenticationFilter.java
    │  │  │  TokenProvider.java
    │  │  │  UserPrincipal.java
    │  │  │  
    │  │  └─oauth2
    │  │      │  CustomOAuth2UserService.java
    │  │      │  HttpCookieOAuth2AuthorizationRequestRepository.java
    │  │      │  OAuth2AuthenticationFailureHandler.java
    │  │      │  OAuth2AuthenticationSuccessHandler.java
    │  │      │  
    │  │      └─user
    │  │              KakaoOAuth2UserInfo.java
    │  │              OAuth2UserInfo.java
    │  │              OAuth2UserInfoFactory.java
    │  │              
    │  ├─domain
    │  │      AuthProvider.java
    │  │      
    │  ├─dto
    │  │      ApiResponse.java
    │  │      AuthResponse.java
    │  │      LoginRequest.java
    │  │      SignUpRequest.java
    │  │      
    │  └─util
    │          CookieUtils.java
    │          
    ├─recommend
    │  ├─application
    │  │      RecommendService.java
    │  │      
    │  ├─domain
    │  │      Recommend.java
    │  │      RecommendImage.java
    │  │      RecommendLike.java
    │  │      RecommendSpot.java
    │  │      
    │  ├─dto
    │  │  ├─command
    │  │  │      RecommendPostRequest.java
    │  │  │      
    │  │  └─event
    │  │          RecommendDetailResponse.java
    │  │          RecommendPreviewDto.java
    │  │          RecommendPreviewResponse.java
    │  │          RecommendScrapResponse.java
    │  │          RecommendSpotScrapResponse.java
    │  │          ScrapAddressSpot.java
    │  │          
    │  ├─presentation
    │  │      RecommendController.java
    │  │      
    │  └─repository
    │          RecommendImageRepository.java
    │          RecommendLikeRepository.java
    │          RecommendRepository.java
    │          RecommendSpotRepository.java
    │          
    ├─review
    │  ├─application
    │  │      ReviewService.java
    │  │      
    │  ├─domain
    │  │      Review.java
    │  │      ReviewImage.java
    │  │      ReviewLike.java
    │  │      
    │  ├─dto
    │  │  │  ReviewDto.java
    │  │  │  ReviewPreviewDto.java
    │  │  │  
    │  │  ├─request
    │  │  │      ReviewCreateRequest.java
    │  │  │      ReviewUpdateRequest.java
    │  │  │      
    │  │  └─response
    │  │          ReviewPreviewsResponse.java
    │  │          ReviewScrapResponse.java
    │  │          ReviewsResponse.java
    │  │          
    │  ├─presentation
    │  │      ReviewController.java
    │  │      
    │  └─repository
    │          ReviewImageRepository.java
    │          ReviewLikeRepository.java
    │          ReviewRepository.java
    │          
    ├─spot
    │  ├─application
    │  │      SpotService.java
    │  │      
    │  ├─domain
    │  │      AthletePickSpot.java
    │  │      Spot.java
    │  │      SpotCategory.java
    │  │      SpotScrap.java
    │  │      
    │  ├─dto
    │  │  │  MapXY.java
    │  │  │  SpotCategoryResponse.java
    │  │  │  SpotDetailInfoDto.java
    │  │  │  SpotMapResponseDto.java
    │  │  │  SpotStadiumPreviewResponse.java
    │  │  │  
    │  │  ├─command
    │  │  │      ScrapResponseDto.java
    │  │  │      ScrapSpot.java
    │  │  │      
    │  │  ├─preview
    │  │  │      SpotAthletePickPreviewDto.java
    │  │  │      SpotBasicPreviewDto.java
    │  │  │      SpotGeneralPreviewDto.java
    │  │  │      
    │  │  └─spotDetailResponse
    │  │          SpotAccommodationDetailResponse.java
    │  │          SpotAthletePickDetailResponse.java
    │  │          SpotCultureDetailResponse.java
    │  │          SpotDetailResponse.java
    │  │          SpotRestaurantDetailResponse.java
    │  │          SpotShoppingDetailResponse.java
    │  │          
    │  ├─presentation
    │  │      SpotDetailController.java
    │  │      SpotMainController.java
    │  │      SpotMapController.java
    │  │      SpotScrapController.java
    │  │      SpotStadiumController.java
    │  │      
    │  └─repository
    │          AthletePickSpotRepository.java
    │          SpotRepository.java
    │          SpotScrapRepository.java
    │          
    ├─stadium
    │  ├─application
    │  │      StadiumService.java
    │  │      
    │  ├─domain
    │  │      Stadium.java
    │  │      
    │  ├─dto
    │  │      StadiumInfo.java
    │  │      StadiumMapXY.java
    │  │      
    │  ├─presentation
    │  │      StadiumController.java
    │  │      
    │  └─repository
    │          StadiumRepository.java
    │          
    ├─tour
    │  ├─dto
    │  │  │  ItemsDeserializer.java
    │  │  │  TourApiDetailCommonResponseDto.java
    │  │  │  TourApiDetailImageResponseDto.java
    │  │  │  TourApiResponseDto.java
    │  │  │  
    │  │  ├─detailIntroResponse
    │  │  │      TourApiAccommodationDetailIntroResponseDto.java
    │  │  │      TourApiCultureDetailIntroResponseDto.java
    │  │  │      TourApiDetailIntroResponseDto.java
    │  │  │      TourApiRestaurantDetailIntroResponseDto.java
    │  │  │      TourApiShoppingDetailIntroResponseDto.java
    │  │  │      
    │  │  └─Enum
    │  │          ContentType.java
    │  │          
    │  └─infrastructure
    │          TourApi.java
    │          
    ├─user
    │  ├─application
    │  │      UserService.java
    │  │      
    │  ├─domain
    │  │      User.java
    │  │      
    │  ├─dto
    │  │  │  UserProfileResponse.java
    │  │  │  UserResisterRequest.java
    │  │  │  UserUpdateRequest.java
    │  │  │  
    │  │  └─event
    │  │          UserDdayDto.java
    │  │          UserInfoDto.java
    │  │          
    │  ├─presentation
    │  │      AuthController.java
    │  │      MypageController.java
    │  │      UserController.java
    │  │      
    │  └─repository
    │          UserRepository.java
    │          
    └─weather
        ├─application
        │      WeatherForecastService.java
        │      
        ├─domain
        │  │  WeatherForecast.java
        │  │  
        │  └─enums
        │          WeatherForecastEnum.java
        │          
        ├─dto
        │      WeatherApiResponse.java
        │      WeatherForecastDTO.java
        │      WeatherForecastPerDayDTO.java
        │      WeatherForecastPerHourDTO.java
        │      
        ├─presentation
        │      WeatherForecastController.java
        │      
        ├─repository
        │      WeatherForecastRepository.java
        │      
        └─scheduler
                WeatherForecastScheduler.java
                
```

<br>

<br>

## 5. 개발 기간 및 작업 관리

### 개발 기간
- 전체 개발 기간 : 2024-04-01 ~ 2024-10-02
- 아이디어 선정 및 기획 : 2024-04-01 ~ 2024-06-30
- 기능 구현 : 2024-07-01 ~ 2024-10-02

<br>
