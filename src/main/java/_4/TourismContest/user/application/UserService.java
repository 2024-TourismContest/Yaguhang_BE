package _4.TourismContest.user.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.exception.ResourceNotFoundException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.oauth.domain.AuthProvider;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserProfileResponse;
import _4.TourismContest.user.dto.UserUpdateRequest;
import _4.TourismContest.user.dto.event.UserDdayDto;
import _4.TourismContest.user.dto.event.UserInfoDto;
import _4.TourismContest.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BaseballRepository baseballRepository;
    private final BaseballScrapRepository baseballScrapRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> teamLogoMap = Map.of(
            "두산", "Doosan.png",
            "LG", "LGTwins.png",
            "KT", "KtWizs.png",
            "SSG", "SSGLanders.png",
            "NC", "NCDinos.png",
            "KIA", "KIA.png",
            "롯데", "Lotte.png",
            "삼성", "Samsung.png",
            "한화", "Hanwha.png",
            "키움", "Kiwoom.png"
    );

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public String createUser(User user) {
        if (userRepository.existsByEmailAndProvider(user.getEmail(), AuthProvider.DEFAULT))
            throw new BadRequestException("Email address already in use.");
        if (userRepository.existsByNickname(user.getNickname()))
            throw new BadRequestException("Nickname already in use.");
        userRepository.save(user);
        return "success create user";
    }

    public UserProfileResponse getCurrentUser(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", uid));
        return UserProfileResponse.of(user);
    }

    public void updateUserPassword(Long id, String password) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.updatePassword(password);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserInfoDto getMypageInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .image(getProfileImg(user.getProfileImg()))
                .fanTeam(getTeamLogoUrl(user.getFanTeam()))
                .fanTeamName(user.getFanTeam())
                .build();

        return userInfoDto;
    }

    private String getProfileImg(String profileImg) {
        if(profileImg == null){
            return "https://yaguhang.kro.kr:8443/defaultLogos/Profile.png";
        }else{
            return profileImg;
        }
    }

    public UserDdayDto getMypageDdayInfo(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Optional<Baseball> optionalBaseball = baseballScrapRepository.findUpcomingBaseballByUser(user);
        if (optionalBaseball.isPresent()) {
            Baseball baseball = optionalBaseball.get();
            UserDdayDto userDdayDto = UserDdayDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .stadium(baseball.getLocation())
                    .home(baseball.getHome())
                    .away(baseball.getAway())
                    .dDay(calculateDday(baseball.getTime()))
                    .date(baseball.getTime().toLocalDate() + " " + baseball.getWeekDay())
                    .gameId(baseball.getId())
                    .build();

            return userDdayDto;
        } else {
            Baseball baseball = baseballRepository.findFirstByTimeIsAfterOrderByTimeAsc(LocalDateTime.now())
                    .orElseThrow(() -> new ResourceNotFoundException("경기일정", "일정 없음", ""));
            UserDdayDto userDdayDto = UserDdayDto.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .stadium(baseball.getLocation())
                    .home(baseball.getHome())
                    .away(baseball.getAway())
                    .dDay(calculateDday(baseball.getTime()))
                    .date(baseball.getTime().toLocalDate() + " " + baseball.getWeekDay())
                    .gameId(baseball.getId())
                    .build();

            return userDdayDto;
        }
    }

    public String calculateDday(LocalDateTime targetDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, targetDate);
        String dDay = "";
        if (duration.toDays() == 0) {
            return "D-Day";
        } else {
            return "D-" + duration.toDays();
        }
    }

    public String registerFanTeam(UserPrincipal userPrincipal, String team) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        userRepository.save(User.registerFanTeam(user, team));

        return "success register";
    }

    private String getTeamLogoUrl(String team) {
        String baseUrl = "https://yaguhang.kro.kr:8443/teamLogos/";
        if (team == null || team.equals(""))
            return baseUrl + "BaseBall.png";
        String logoFileName = teamLogoMap.get(team);

        if (logoFileName == null) {
            throw new IllegalArgumentException("Unknown team: " + team + ". Please check the team name.");
        }

        return baseUrl + logoFileName;
    }

    @Transactional
    public UserInfoDto updateUser(Long currentUserId, UserUpdateRequest request) {
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        user.update(request);

        return UserInfoDto.of(user);
    }

    @Transactional
    public String checkFanTeam(UserPrincipal userPrincipal) {
        if (userPrincipal == null) return "not logined";
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        if (user.getFanTeam() != null) {
            return user.getFanTeam();
        } else if (user.getFanTeam() == null && !user.isWannaCheckFanTeam()) {
            return "No Check";
        }
        return "Check";
    }

    @Transactional
    public String clickWannaCheckFanTeam(UserPrincipal userPrincipal) {
        if (userPrincipal == null) return "not logined";
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        if (user.isWannaCheckFanTeam()) {
            user.noWannaCheckFanTeam();
            return "success spam checkFanTeam";
        } else {
            user.wannaCheckFanTeam();
            return "success wanna checkFanTeam";
        }
    }
}
