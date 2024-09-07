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
    public ResponseEntity<UserProfileResponse> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        UserProfileResponse currentUser = userService.getCurrentUser(userPrincipal.getId());
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping
    @Operation(summary = "사용자 프로필 수정", description = "닉네임, 이미지 수정 가능")
    public ResponseEntity<UserInfoDto> updateUser(@CurrentUser UserPrincipal user, @RequestBody UserUpdateRequest request) {
        UserInfoDto response = userService.updateUser(user.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser(@CurrentUser UserPrincipal user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.ok("success delete user");
    }


    @PostMapping("/fan/{team}")
    @Operation(summary = "팬 구단 등록", description = "구단명 전체 입력하면 됩니다. 등록/수정 가능. ")
    public ResponseEntity<String> registerFanTeam(@CurrentUser UserPrincipal userPrincipal, @PathVariable String team) {
        return new ResponseEntity<>(userService.registerFanTeam(userPrincipal, team), HttpStatus.OK);
    }
}