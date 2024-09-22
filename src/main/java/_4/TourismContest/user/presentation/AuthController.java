package _4.TourismContest.user.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.CustomUsernamePasswordAuthenticationToken;
import _4.TourismContest.oauth.application.TokenProvider;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.oauth.domain.AuthProvider;
import _4.TourismContest.oauth.dto.AuthResponse;
import _4.TourismContest.oauth.dto.LoginRequest;
import _4.TourismContest.user.application.UserService;
import _4.TourismContest.user.domain.User;
import _4.TourismContest.user.dto.UserResisterRequest;
import _4.TourismContest.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "로그인" ,description = "")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new CustomUsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword(),
                        AuthProvider.DEFAULT
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입"
            ,description = "이메일 형식으로 기입 할것.\n" +
            "비밀번호는 6~20자 사이\n" +
            "닉네임은 2~30자 사이로 입력해주세요.")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserResisterRequest registrationDto) {
        User newUser = User.builder()
                .email(registrationDto.email())
                .password(passwordEncoder.encode(registrationDto.password()))
                .nickname(registrationDto.nickname())
                .provider(AuthProvider.DEFAULT)
                .build();

        String result = userService.createUser(newUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/provider")
    @Operation(summary = "로그인 provider 확인" ,description = "로그인 종류를 확인합니다.(일반, 카카오)")
    public ResponseEntity<String> getUserProvider(@CurrentUser UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));
        return ResponseEntity.ok(user.getProvider().name());
    }

    @GetMapping("/password-check")
    @Operation(summary = "비밀번호 확인" ,description = "")
    public ResponseEntity<Boolean> checkPassword(@CurrentUser UserPrincipal currentUser, @RequestBody String password) {
        return ResponseEntity.ok(currentUser.getPassword().equals(password));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경" ,description = "")
    public ResponseEntity<String> updatePassword(@CurrentUser UserPrincipal currentUser, @RequestBody String password) {
        userService.updateUserPassword(currentUser.getId(), passwordEncoder.encode(password));
        return ResponseEntity.ok("success update password");
    }
}

