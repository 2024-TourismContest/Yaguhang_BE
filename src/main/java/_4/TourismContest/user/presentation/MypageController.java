package _4.TourismContest.user.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.user.application.UserService;
import _4.TourismContest.user.dto.UserProfileResponse;
import _4.TourismContest.user.dto.event.UserDdayDto;
import _4.TourismContest.user.dto.event.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final UserService userService;
    @GetMapping("/info")
    @Operation(summary = "마이페이지에서 유저 정보 가져오기" ,description = "로그인 토큰만 보내면 됩니다.")
    public ResponseEntity<UserInfoDto> getUserInfo(@CurrentUser UserPrincipal userPrincipal) {
        UserInfoDto userInfoDto = userService.getMypageInfo(userPrincipal);
        return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
    }

    @GetMapping("/dday")
    @Operation(summary = "마이페이지 상단 스크랩한 경기 일정의 D-day 가져오기" ,description = "로그인 토큰만 보내면 됩니다. / 스크랩한 경기가 없을 시 가장 가까운 경기 일정을 보여줍니다. 수정 필요하면 말하세여")
    public ResponseEntity<UserDdayDto> getUserDday(@CurrentUser UserPrincipal userPrincipal) {
        UserDdayDto userDdayDto = userService.getMypageDdayInfo(userPrincipal);
        return new ResponseEntity<>(userDdayDto, HttpStatus.OK);
    }
}
