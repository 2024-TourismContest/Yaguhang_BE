package _4.TourismContest.baseball.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.domain.BaseballScrap;
import _4.TourismContest.baseball.dto.BaseBallScrapResponseDTO;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.tour.dto.TourApiResponseDto;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseballScrapService {
    private final UserRepository userRepository;
    private final BaseballScrapRepository baseballScrapRepository;
    private final BaseballRepository baseballRepository;

    /**
     * 경기 스크랩 여부
     * @param userPrincipal
     * @param gameId
     * @return
     */
    public Boolean getIsScrapped(UserPrincipal userPrincipal, Long gameId){
        if(userPrincipal == null){
            //로그인 정보가 없을 시
            return false;
        }else{
            User user = userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));
            Baseball baseball = baseballRepository.findById(gameId)
                    .orElseThrow(() -> new IllegalArgumentException("경기 ID를 다시 확인해주세요"));
            Optional<BaseballScrap> baseballScrapOptional = baseballScrapRepository.findByBaseballAndUser(baseball, user);
            if(baseballScrapOptional.isEmpty()){
                return false;
            }else{
                return true;
            }
        }
    }

    /**
     * 경기 스크랩하기
     * 만약 기존에 스크랩 되어 있던 경우 -> 스크랩 취소
     * 스크랩 되어 있지 않는 경우 -> 스크랩
     * @param userPrincipal
     * @param gameId
     * @return
     */
    @Transactional
    public BaseBallScrapResponseDTO createScrap(UserPrincipal userPrincipal, Long gameId) {
        if (userPrincipal == null) {
            throw new BadRequestException("유저 토큰 값을 넣어주세요");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));

        Baseball baseball = baseballRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("경기 ID를 다시 확인해주세요"));

        Optional<BaseballScrap> baseballScrapOptional = baseballScrapRepository.findByBaseballAndUser(baseball, user);

        if (baseballScrapOptional.isPresent()) {
            throw new IllegalStateException("기존에 스크랩 되어 있는 경기입니다.");
        }

        // 새롭게 스크랩을 할 경우
        BaseballScrap baseballScrap = BaseballScrap.builder()
                .user(user)
                .baseball(baseball)
                .build();

        baseballScrapRepository.save(baseballScrap);

        return BaseBallScrapResponseDTO.builder()
                .gameId(baseball.getId())
                .isScrapped(true)
                .build();
    }

    /**
     * 스크랩 한 경기를 취소하는 메서드
     * @param userPrincipal
     * @param gameId
     * @return
     */
    @Transactional
    public BaseBallScrapResponseDTO deleteScrap(UserPrincipal userPrincipal, Long gameId) {
        if (userPrincipal == null) {
            throw new BadRequestException("유저 토큰 값을 넣어주세요");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new BadRequestException("유저 토큰 값을 다시 확인해주세요"));

        Baseball baseball = baseballRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("경기 ID를 다시 확인해주세요"));

        BaseballScrap baseballScrap = baseballScrapRepository.findByBaseballAndUser(baseball, user)
                .orElseThrow(() -> new IllegalStateException("해당 경기는 스크랩 되어 있지 않습니다."));

        baseballScrapRepository.delete(baseballScrap);

        return BaseBallScrapResponseDTO.builder()
                .gameId(baseball.getId())
                .isScrapped(false)
                .build();
    }
}
