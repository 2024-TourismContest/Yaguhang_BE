package _4.TourismContest.recommend.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.recommend.domain.RecommendImage;
import _4.TourismContest.recommend.domain.RecommendLike;
import _4.TourismContest.recommend.domain.RecommendSpot;
import _4.TourismContest.recommend.dto.command.RecommendPostRequest;
import _4.TourismContest.recommend.dto.event.*;
import _4.TourismContest.recommend.repository.RecommendImageRepository;
import _4.TourismContest.recommend.repository.RecommendLikeRepository;
import _4.TourismContest.recommend.repository.RecommendRepository;
import _4.TourismContest.recommend.repository.RecommendSpotRepository;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.domain.Spot;
import _4.TourismContest.spot.domain.SpotScrap;
import _4.TourismContest.spot.dto.command.ScrapSpot;
import _4.TourismContest.spot.dto.command.ScrapStadium;
import _4.TourismContest.spot.dto.command.ScrapStadiumSpot;
import _4.TourismContest.spot.dto.preview.SpotGeneralPreviewDto;
import _4.TourismContest.spot.repository.SpotScrapRepository;
import _4.TourismContest.stadium.domain.Stadium;
import _4.TourismContest.stadium.repository.StadiumRepository;
import _4.TourismContest.tour.dto.TourApiDetailCommonResponseDto;
import _4.TourismContest.tour.infrastructure.TourApi;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final RecommendSpotRepository recommendSpotRepository;
    private final RecommendLikeRepository recommendLikeRepository;
    private final RecommendImageRepository recommendImageRepository;
    private final SpotScrapRepository spotScrapRepository;
    private final StadiumRepository stadiumRepository;
    private final UserRepository userRepository;
    private final TourApi tourApi;
    public RecommendPreviewResponse getRecommendList(Integer pageIndex,  Integer pagesize, String order, String filter, UserPrincipal userPrincipal){
        Pageable pageable;
        if(order.equals("최신순")){
            pageable = PageRequest.of(pageIndex, pagesize, Sort.by("createdAt").descending());
        }
        else if(order.equals("인기순")){
            pageable = PageRequest.of(pageIndex, pagesize, Sort.by("likeCount").descending());
        }
        else{
            throw new BadRequestException("정렬 기준을 다시 확인해주세요");
        }

        Page<Recommend> recommendsPage;
        if(filter.equals("전체")) {
            recommendsPage = recommendRepository.findRecommendList(pageable);
        }
        else{
            recommendsPage = recommendRepository.findRecommendListByfilter(pageable, filter);
        }

        List<RecommendPreviewDto> recommendPreviewDtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        for (Recommend recommend : recommendsPage.getContent()) {
            List<String> imageList = recommend.getRecommendImages().stream()
                    .map(RecommendImage::getImage)
                    .filter(image -> image != null && !image.isEmpty())
                    .collect(Collectors.toList());

            Random random = new Random();
            String profileImg = imageList.get(random.nextInt(imageList.size()));
            RecommendPreviewDto recommendPreviewDto = RecommendPreviewDto.builder()
                    .recommendId(recommend.getId())
                    .stadiumName(recommend.getStadium().getName())
                    .authorName(recommend.getUser().getNickname())
                    .profileImage(profileImg)
                    .title(recommend.getTitle())
                    .images(imageList)
                    .createdAt(recommend.getCreatedAt().format(formatter))
                    .isMine(isMine(userPrincipal, recommend))
                    .likes(recommend.getLikeCount())
                    .isLiked(isScraped(userPrincipal, recommend))
                    .build();
            recommendPreviewDtos.add(recommendPreviewDto);
        }

        RecommendPreviewResponse recommendPreviewResponse = RecommendPreviewResponse.builder()
                .hasNextPage(recommendsPage.hasNext())
                .pagesize(pagesize)
                .recommendPreviewDtos(recommendPreviewDtos)
                .build();
        return recommendPreviewResponse;
    }

    public RecommendPreviewResponse getMyRecommendList(Integer pagesize, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
        Pageable pageable = PageRequest.of(0, pagesize);
        List<Recommend> recommends = recommendRepository.findRecommendByUser(user.getId(), pageable);
        List<RecommendPreviewDto> recommendPreviewDtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        for (Recommend recommend : recommends) {
            List<String> imageList = recommend.getRecommendImages().stream()
                    .map(RecommendImage::getImage)
                    .filter(image -> image != null && !image.isEmpty())
                    .collect(Collectors.toList());

            Random random = new Random();
            String profileImg = imageList.get(random.nextInt(imageList.size()));

            RecommendPreviewDto recommendPreviewDto = RecommendPreviewDto.builder()
                    .recommendId(recommend.getId())
                    .authorName(recommend.getUser().getNickname())
                    .profileImage(profileImg)
                    .title(recommend.getTitle())
                    .images(imageList)
                    .createdAt(recommend.getCreatedAt().format(formatter))
                    .isMine(isMine(userPrincipal, recommend))
                    .likes(recommend.getLikeCount())
                    .isLiked(isScraped(userPrincipal, recommend))
                    .build();
            recommendPreviewDtos.add(recommendPreviewDto);
        }

        RecommendPreviewResponse recommendPreviewResponse = RecommendPreviewResponse.builder()
                .pagesize(pagesize)
                .recommendPreviewDtos(recommendPreviewDtos)
                .build();
        return recommendPreviewResponse;
    }

    public RecommendDetailResponse getRecommendDetail(Long recommendId, UserPrincipal userPrincipal){
        Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(() -> new BadRequestException("recommendId를 다시 확인해주세요"));
        List<RecommendSpot> recommendSpots = recommendSpotRepository.findByRecommend(recommend);
        List<SpotGeneralPreviewDto> spotGeneralPreviewDtos = new ArrayList<>();
        for(RecommendSpot recommendSpot : recommendSpots){
            SpotGeneralPreviewDto spotGeneralPreviewDto = SpotGeneralPreviewDto.builder()
                    .contentId(recommendSpot.getSpot().getId())
                    .name(recommendSpot.getSpot().getName())
                    .address(recommendSpot.getSpot().getAddress())
                    .imageUrl(recommendSpot.getSpot().getImage())
                    .isScraped(isScrapedSpot(userPrincipal, recommendSpot.getSpot()))
                    .build();
            spotGeneralPreviewDtos.add(spotGeneralPreviewDto);
        }

        return RecommendDetailResponse.builder()
                .recommendId(recommendId)
                .authorName(recommend.getUser().getNickname())
                .title(recommend.getTitle())
                .likes(recommend.getLikeCount())
                .createdAt(recommend.getCreatedAt())
                .profileImage(recommend.getUser().getProfileImg())
                .isMine(isMine(userPrincipal,recommend))
                .isLiked(isScraped(userPrincipal,recommend))
                .spotGeneralPreviewDtos(spotGeneralPreviewDtos)
                .build();
    }

    @Transactional
    public String likeRecommend(Long recommendId, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
        Recommend recommend = recommendRepository.findById(recommendId)
                .orElseThrow(() -> new BadRequestException("recommendId를 다시 확인해주세요"));
        Optional<RecommendLike> optionalRecommendLike = recommendLikeRepository.findRecommendLikeByUserAndRecommend(user, recommend);

        if(optionalRecommendLike.isPresent()){
            RecommendLike recommendLike = optionalRecommendLike.get();
            recommendLikeRepository.delete(recommendLike);
            recommend.minusLikes(recommend);
            return "remove like";
        }
        else{
            RecommendLike recommendLike = RecommendLike.builder()
                    .user(user)
                    .recommend(recommend)
                    .build();
            recommendLikeRepository.save(recommendLike);
            recommend.plusLikes(recommend);
            return "add like";
        }
    }
    public RecommendSpotScrapResponse getrecommendSpotScrapResponse(String stadiumName, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
        return RecommendSpotScrapResponse.builder()
                .stadium(stadiumName)
                .scrapAddressSpots(getScrapStadiumSpot(user, stadiumName))
                .build();
    }

    public List<ScrapAddressSpot> getScrapStadiumSpot(User user, String name){
        List<SpotScrap> spotScraps = spotScrapRepository.findByUserIdAndName(user.getId(), name);
        if (spotScraps == null || spotScraps.isEmpty()) {
            return null;
        }


        List<ScrapAddressSpot> scrapAddressSpots = new ArrayList<>();
        for(SpotScrap spotScrap : spotScraps){
            ScrapAddressSpot scrapAddressSpot = ScrapAddressSpot.builder()
                    .address(spotScrap.getSpot().getAddress())
                    .title(spotScrap.getSpot().getName())
                    .contentId(spotScrap.getSpot().getId())
                    .image(spotScrap.getSpot().getImage())
                    .build();
            scrapAddressSpots.add(scrapAddressSpot);
        }
        return scrapAddressSpots;
    }


    public boolean isScrapedSpot(UserPrincipal userPrincipal, Spot spot){
        if(userPrincipal == null)
            return false;
        else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
            Optional<SpotScrap> optionalSpotScrap = spotScrapRepository.findByUserIdAndSpotContentId(user.getId(), spot.getId());
            if(optionalSpotScrap.isPresent())
                return true;
            else
                return false;
        }
    }

    public boolean isMine(UserPrincipal userPrincipal, Recommend recommend){
        if(userPrincipal == null)
            return false;
        else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
            if(user == recommend.getUser())
                return true;
            else
                return false;
        }
    }

    @Transactional
    public String postRecommend(RecommendPostRequest recommendPostRequest, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
        Stadium stadium = stadiumRepository.findTopByName(recommendPostRequest.Stadium())
                .orElseThrow(() -> new BadRequestException("경기장 이름을 다시 확인해주세요"));

        Recommend recommend = recommendRepository.save(Recommend.builder()
                .stadium(stadium)
                .user(user)
                .title(recommendPostRequest.title())
                .build());

        List<RecommendImage> recommendImages = new ArrayList<>();

        for(Long contentId : recommendPostRequest.contentIdList()){
            SpotScrap spot = spotScrapRepository.findByUserIdAndSpotContentId(user.getId(), contentId)
                    .orElseThrow(() -> new BadRequestException("contentId를 다시 확인해주세요"));
            RecommendSpot recommendSpot = RecommendSpot.builder()
                    .recommend(recommend)
                    .spot(spot.getSpot())
                    .build();

            if(spot.getSpot().getImage()!=null) {
                recommendImages.add(recommendImageRepository.save(RecommendImage.builder()
                        .image(spot.getSpot().getImage())
                        .build()));
            }
            recommendSpotRepository.save(recommendSpot);
        }
        recommendRepository.save(recommend.setImages(recommend, recommendImages));
        return "success post recommend";
    }
    @Transactional
    public String deleteRecommend(Long recommendId, UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
        Recommend recommend =  recommendRepository.findById(recommendId)
                .orElseThrow(() -> new BadRequestException("recommendId를 다시 확인해주세요"));
        if(user.getId() != recommend.getUser().getId()){
            new BadRequestException("삭제 권한이 없습니다. ");
        }
        List<RecommendSpot> recommendSpots = recommendSpotRepository.findByRecommend(recommend);
        for(RecommendSpot recommendSpot : recommendSpots)
            recommendSpotRepository.delete(recommendSpot);
        recommendRepository.delete(recommend);
        return "success post recommend";
    }


    public boolean isScraped(UserPrincipal userPrincipal, Recommend recommend){
        if(userPrincipal == null)
            return false;
        else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("로그인 토큰을 다시 확인해주세요"));
            Optional<RecommendLike> optionalRecommendLike = recommendLikeRepository.findRecommendLikeByUserAndRecommend(user, recommend);
            if(optionalRecommendLike.isPresent())
                return true;
            else
                return false;
        }

    }


}