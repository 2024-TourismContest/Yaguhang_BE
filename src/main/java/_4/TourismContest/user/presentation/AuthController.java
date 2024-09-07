package _4.TourismContest.user.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.TokenProvider;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.oauth.dto.AuthResponse;
import _4.TourismContest.oauth.dto.LoginRequest;
import _4.TourismContest.user.application.UserService;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserResisterRequest;
import _4.TourismContest.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserResisterRequest registrationDto) {
        User newUser = User.builder()
                .email(registrationDto.email())
                .password(passwordEncoder.encode(registrationDto.password()))
                .nickname(registrationDto.nickname())
                .build();

        String result = userService.createUser(newUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/provider")
    public ResponseEntity<String> getUserProvider(@CurrentUser UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));
        return ResponseEntity.ok(user.getProvider().name());
    }

    @GetMapping("/password-check")
    public ResponseEntity<Boolean> checkPassword(@CurrentUser UserPrincipal currentUser, @RequestBody String password) {
        return ResponseEntity.ok(currentUser.getPassword().equals(password));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@CurrentUser UserPrincipal currentUser, @RequestBody String password) {
        userService.updateUserPassword(currentUser.getId(), passwordEncoder.encode(password));
        return ResponseEntity.ok("success update password");
    }
}

