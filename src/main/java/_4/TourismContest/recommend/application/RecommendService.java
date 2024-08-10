package _4.TourismContest.recommend.application;

import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.recommend.domain.Recommend;
import _4.TourismContest.recommend.domain.RecommendLike;
import _4.TourismContest.recommend.dto.event.RecommendPreviewDto;
import _4.TourismContest.recommend.dto.event.RecommendPreviewResponse;
import _4.TourismContest.recommend.repository.RecommendLikeRepository;
import _4.TourismContest.recommend.repository.RecommendRepository;
import _4.TourismContest.recommend.repository.RecommendSpotRepository;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final RecommendSpotRepository recommendSpotRepository;
    private final RecommendLikeRepository recommendLikeRepository;
    private final UserRepository userRepository;
    public RecommendPreviewResponse getRecommendList(Long stadiumId,  Integer pagesize, UserPrincipal userPrincipal){
        Pageable pageable = PageRequest.of(0, pagesize, Sort.by("likeCount").descending());
        List<Recommend> recommends = recommendRepository.findByLikes(stadiumId, pageable);
        List<RecommendPreviewDto> recommendPreviewDtos = new ArrayList<>();

        if(userPrincipal == null){
            for(Recommend recommend : recommends){
                RecommendPreviewDto recommendPreviewDto = RecommendPreviewDto.builder()
                        .recommendId(recommend.getId())
                        .authorName(recommend.getUser().getNickname())
                        .profileImage(recommend.getUser().getProfileImg())
                        .title(recommend.getTitle())
                        .image(recommend.getImage())
                        .createdAt(recommend.getCreatedAt())
                        .isMine(false)
                        .likes(recommend.getLikeCount())
                        .isLiked(false)
                        .build();
                recommendPreviewDtos.add(recommendPreviewDto);
            }
        }
        else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));

            for(Recommend recommend : recommends){
                RecommendPreviewDto recommendPreviewDto = RecommendPreviewDto.builder()
                        .recommendId(recommend.getId())
                        .authorName(recommend.getUser().getNickname())
                        .profileImage(recommend.getUser().getProfileImg())
                        .title(recommend.getTitle())
                        .image(recommend.getImage())
                        .createdAt(recommend.getCreatedAt())
                        .isMine(user.getId()==recommend.getUser().getId())
                        .likes(recommend.getLikeCount())
                        .isLiked(isScraped(user, recommend))
                        .build();
                recommendPreviewDtos.add(recommendPreviewDto);
            }
        }

        RecommendPreviewResponse recommendPreviewResponse = RecommendPreviewResponse.builder()
                .pagesize(pagesize)
                .recommendPreviewDtos(recommendPreviewDtos)
                .build();
        return recommendPreviewResponse;
    }

    public boolean isScraped(User user, Recommend recommend){
        Optional<RecommendLike> optionalRecommendLike = recommendLikeRepository.findRecommendLikeByUserAndRecommend(user, recommend);
        if(optionalRecommendLike.isPresent())
            return true;
        else
            return false;
    }


}