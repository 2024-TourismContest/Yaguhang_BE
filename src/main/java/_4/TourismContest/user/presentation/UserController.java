package _4.TourismContest.user.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.user.application.UserService;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> user = userService.updateUser(id, updatedUser);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/fan/{team}")
    @Operation(summary = "팬 구단 등록" ,description = "구단명 전체 입력하면 됩니다. 등록/수정 가능. ")
    public ResponseEntity<String> registerFanTeam(@CurrentUser UserPrincipal userPrincipal, @PathVariable String team){
        return new ResponseEntity<>(userService.registerFanTeam(userPrincipal, team), HttpStatus.OK);
    }
}