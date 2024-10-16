# ⚾ 야구보고 ✈️ 여행도 가고!  
### **야구팬들을 위한 특별한 웹서비스, 야구행**


![ServiceInfo](https://github.com/user-attachments/assets/3589ee2f-1e58-4de5-a78c-75a67a085273)


- 배포 URL : https://yaguhang.kro.kr:7443
- Test ID : yaguhang@test.com
- Test PW : yaguhangTest!

<br>

## 프로젝트 소개

- 야구행은 야구 경기를 관람하고, 그 열기를 간직한 채 바로 여행을 떠날 수 있는 맞춤형 웹서비스입니다.
- 야구를 사랑하는 팬들을 위해, 경기를 즐긴 후 인근 지역에서의 여행 일정까지 한 번에 계획할 수 있도록 도와드립니다.
- 야구 경기 일정과 해당 경기의 날씨 정보를 확인할 수 있습니다.
- 야구 경기 스크랩을 통해 관심 있는 경기를 추후 쉽게 확인할 수 있습니다.
- 스크랩한 경기 일정은 하루 전과 당일, 로그인한 이메일로 푸시 알림을 보내드려 중요한 경기를 놓치지 않도록 도와드립니다.
- TourAPI를 통해 야구 경기장 인근에서 선수들이 추천하는 맛집, 숙소, 음식, 문화, 쇼핑 명소를 안내해드립니다.
- 지도를 이용한 주변 관광지 탐색 기능 지도를 활용해 경기장 주변의 추천 명소(맛집, 숙소, 음식, 문화, 쇼핑)를 쉽게 탐색할 수 있습니다.
- 관광지에 대한 상세 정보(주소, 리뷰, 영업시간 등)를 제공합니다.
- 주변 관광지 리뷰, 좋아요, 스크랩 기능을 통해 관광지에 대한 리뷰를 남기고, 좋아요를 누르며, 마음에 드는 장소는 스크랩할 수 있습니다.
- 추천 여행지 목록을 제작하여 나만의 맞춤형 여행지를 다른 사용자들과 공유할 수 있습니다.

<br>

## 팀원 구성
### FrontEnd
<div align="center">

| **김예진** | **오아영** | **임성경** |
| :------: |  :------: | :------: |
| [<img src="https://avatars.githubusercontent.com/u/77667199?v=4" height=150 width=150> <br/> @dani001024](https://github.com/dani001024) | [<img src="https://avatars.githubusercontent.com/u/110577667?v=4" height=150 width=150> <br/> @aong13](https://github.com/aong13) | [<img src="https://avatars.githubusercontent.com/u/131395142?v=4" height=150 width=150> <br/> @5622lsk](https://github.com/5622lsk) |

</div>


### BackEnd
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

## 2. 채택한 개발 기술과 브랜치 전략
### 브랜치 전략

- Git-flow 전략을 기반으로 develop, HotFix 브랜치와 feature 보조 브랜치를 운용했습니다.
- develop, HotFix, Feat 브랜치로 나누어 개발을 하였습니다.
    - **develop** 브랜치는 개발 단계에서 git-flow의 master 역할을 하는 브랜치입니다.
    - **Feat** 브랜치는 기능 단위로 독립적인 개발 환경을 위하여 사용하였습니다.
    - **HotFix** 브랜치는 develop 브랜치로 배포를 하고 나서 심각한 버그가 생겼을때 수정하기 위해 사용하였습니다.

<br>

## 3. 역할 분담

### 최민석

- **기능**
    - 야구 경기 일정(KBO), 구단 주변 날씨 정보(기상청) 데이터 수집, 지도 기반 주변 관광지 탐색(카카오 맵), 야구 경기 일정 스크랩
    - CI/CD 설정, 웹서버 및 데이터베이스 구축

### 김종경

- **기능**
    - 주변 관광지 카테고리별 미리보기/상세보기(Tour API), 주변 관광지 스크랩, 마이페이지, 사용자 추천 주변관광지 리스트, 팬 구단

### 양두영

- **기능**
    - 회원 관리 기능(KaKao Oauth2.0, 일반), 주변 관광지 리뷰, 야구선수 픽 맛집 기능 및 데이터 수집

<br>

## 5. 개발 기간 및 작업 관리

### 개발 기간
- 전체 개발 기간 : 2024-04-01 ~ 2024-10-02
- 아이디어 선정 및 기획 : 2024-04-01 ~ 2024-06-30
- 기능 구현 : 2024-07-01 ~ 2024-10-02

<br>

## 6. 페이지별 기능

### [카카오 로그인]
- Spring Security & OAuth2.0을 사용하여 카카오 로그인을 수행합니다.
- 이미 회원가입된 상태라면 email 등 유저정보를 기반으로 토큰을 생성하여 반환합니다.
- 회원가입 되어 있지 않은 상태라면 카카오 로그인 페이지로 리다이렉션하여 로그인을 수행하고 반환받은 정보를 기반으로 유저 정보를 저장합니다.

| 카카오 로그인 |
|----------|
|<img src="https://github.com/user-attachments/assets/14ccc1ef-a831-453b-b0e8-7fd759f9a92f" alt="login" width="1280">|

<br>

### [경기일정 조회 및 스크랩]
- 메인페이지에서 원하는 경기일정의 날짜를 조회할 수 있습니다. 
- 로그인 후 팬 구단을 설정한 경우 유저의 팬 구단 기반으로 경기 일정을 조회할 수 있습니다.
- 야구공모양 버튼을 클릭해서 원하는 경기를 스크랩합니다. 스크랩 된 경기는 빨간색 야구공 아이콘으로 표시됩니다.
- '야구공 스탬프 모아보기' 버튼을 클릭하면 마이페이지에서 내가 스크랩한 경기들을 모아 볼 수 있습니다.

| 경기일정별 조회 및 스크랩 |
|----------|
|<img src="https://github.com/user-attachments/assets/aaafee34-a9db-41d6-9e54-d8d95e62c398" alt="login" width="1280">|

<br>

### [경기일정별 구단 날씨 조회]
- 야구 경기 일정과 간단한 날씨정보를 카드 형태로 확인할 수 있습니다.(KBO리그 데이터와 기상청 예보 API 활용)
- 경기를 선택한 상태에서 메인 페이지 하단으로 스크롤을 내리면 해당 경기의 날씨 정보와 시간별 기온, 강수량을 확인할 수 있습니다.

| 경기일정별 구단 날씨 조회 |
|----------|
|<img src="https://github.com/user-attachments/assets/7a1b311f-b4bd-4570-a718-d7adb3f92019" alt="login" width="1280">|

<br>

### [구장행]
- 구장행에서 주변관광지를 탐색하고 싶은 구단을 선택합니다.
- 구단을 중점으로 (야구선수 맛집, 숙소, 맛집, 쇼핑, 문화)카테고리로 구분되어 있는 주변관광지 목록을 확인할 수 있습니다. (Tour API)

| 구장행 |
|----------|
|<img src="https://github.com/user-attachments/assets/569a77f3-3d82-44b5-9d4d-36006871b5f6" alt="login" width="1280">|

<br>

### [추천행]
- 추천행 페이지에서 다른 사람이 추천한 관광지 목록을 확인할 수 있습니다.
- 추천행 페이지에서 각 리스트들의 우측에 위치한 ▼ 화살표를 눌러 관광지 목록을 확인할 수 있습니다.
- 상단 “나의 추천행 코스 만들기” 버튼을 클릭하여, 본인의 추천행을 작성할 수 있습니다.

| 추천행 |
|----------|
|<img src="https://github.com/user-attachments/assets/860db4a2-ffae-4307-a9cb-ddc14d68aacb" alt="login" width="1280">|

<br>

### [마이페이지]
- '마이페이지>나의계정관리'탭에서 사용자 정보를 수정할 수 있습니다.
- 마이페이지에서 내가 스크랩한 경기 일정 모아서 확인할 수 있습니다.
- 마이페이지에서 내가 작성한 추천행을 모아서 확인할 수 있습니다.
- 마이페이지에서 내가 스크랩한 북마크를 모아서 확인할 수 있습니다.
- 마이페이지에서 내가 작성한 리뷰를 모아서 확인할 수 있습니다.

| 마이페이지 |
|----------|
|<img src="https://github.com/user-attachments/assets/2fe8db5b-08f1-49c7-88f7-e15a3b80cfd7" alt="login" width="1280">|

<br>
