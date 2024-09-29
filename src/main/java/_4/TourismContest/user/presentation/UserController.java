package _4.TourismContest.user.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.user.application.UserService;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserProfileResponse;
import _4.TourismContest.user.dto.UserUpdateRequest;
import _4.TourismContest.user.dto.event.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "내 정보 확인", description = "")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        UserProfileResponse currentUser = userService.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping
    @Operation(summary = "사용자 프로필 수정", description = "닉네임, 이미지 수정 가능")
    public ResponseEntity<UserInfoDto> updateUser(@CurrentUser UserPrincipal user, @RequestBody UserUpdateRequest request) {
        UserInfoDto response = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "토큰만 넣어보내면 됩니다.")
    public ResponseEntity<String> deleteUser(@CurrentUser UserPrincipal userPrincipal) {
        userService.deleteUser(userPrincipal);
        return ResponseEntity.ok("success delete user");
    }


    @PostMapping("/fan/{team}")
    @Operation(summary = "팬 구단 등록", description = "구단명 전체 입력하면 됩니다. 등록/수정 가능. ")
    public ResponseEntity<String> registerFanTeam(@CurrentUser UserPrincipal userPrincipal, @PathVariable String team) {
        return new ResponseEntity<>(userService.registerFanTeam(userPrincipal, team), HttpStatus.OK);
    }

    @GetMapping("/check/fan-team")
    @Operation(summary = "팬 구단이 등록 확인",
            description = "등록 되어 있다면 팀 이름을 반환합니다.\n" +
                    "등록 되어 있지 않고, 등록을 요구한다면 \"Check\"을 반환합니다.\n" +
                    "이때, 다시 보지 않기로 되어 있다면 \"No Check\"을 반환합니다.")
    public ResponseEntity<String> checkFanTeam(@CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(userService.checkFanTeam(userPrincipal), HttpStatus.OK);
    }

    @PatchMapping("/click/check-fan-team")
    @Operation(summary = "팬 구단 등록 요청 설정", description = "사용자가 팬 구단이 등록 요청을 띄우지 않도록 설정합니다")
    public ResponseEntity<String> clickWannaCheckFanTeam(@CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<>(userService.clickWannaCheckFanTeam(userPrincipal), HttpStatus.OK);
    }
}
