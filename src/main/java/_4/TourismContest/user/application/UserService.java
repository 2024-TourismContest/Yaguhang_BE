package _4.TourismContest.user.application;

import _4.TourismContest.baseball.domain.Baseball;
import _4.TourismContest.baseball.repository.BaseballRepository;
import _4.TourismContest.baseball.repository.BaseballScrapRepository;
import _4.TourismContest.baseball.repository.impl.BaseballScrapRepositoryCustom;
import _4.TourismContest.baseball.repository.impl.BaseballScrapRepositoryCustomImpl;
import _4.TourismContest.exception.BadRequestException;
import _4.TourismContest.exception.ResourceNotFoundException;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserProfileResponse;
import _4.TourismContest.user.dto.event.UserDdayDto;
import _4.TourismContest.user.dto.event.UserInfoDto;
import _4.TourismContest.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BaseballRepository baseballRepository;
    private final BaseballScrapRepository baseballScrapRepository;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public String createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }
        userRepository.save(user);
        return "success create user";
    }

    public UserProfileResponse getCurrentUser(Long uid){
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", uid));
        return UserProfileResponse.of(user);
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            User updated = User.builder()
                    .email(updatedUser.getEmail())
                    .password(updatedUser.getPassword())
                    .nickname(updatedUser.getNickname())
                    .profileImg(updatedUser.getProfileImg())
                    .build();
            return userRepository.save(updated);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserInfoDto getMypageInfo(UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .image(user.getProfileImg())
                .fanTeam(user.getFanTeam())
                .build();

        return userInfoDto;
    }

    public UserDdayDto getMypageDdayInfo(UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        Optional<Baseball> optionalBaseball = baseballScrapRepository.findUpcomingBaseballByUser(user);
        if(optionalBaseball.isPresent()){
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
        }
        else {
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
        if(duration.toDays() == 0){
            return "D-Day";
        }
        else{
            return "D-" + duration.toDays();
        }
    }

    public String registerFanTeam(UserPrincipal userPrincipal, String team){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        userRepository.save(User.registerFanTeam(user, team));

        return "success register";
    }
}