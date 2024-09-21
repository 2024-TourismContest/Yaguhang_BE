package _4.TourismContest.user.domain;

import _4.TourismContest.oauth.domain.AuthProvider;
import _4.TourismContest.user.dto.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", updatable = false, nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "fanTeam")
    private String fanTeam;

    @Column(name = "wannaCheckFanTeam")
    private boolean wannaCheckFanTeam;

    @Builder
    public User(String email, String password, String nickname, String profileImg) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.wannaCheckFanTeam = true;
    }

    public static User createOAuthUser(String email, String nickname, String profileImg, AuthProvider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.profileImg = profileImg;
        user.provider = provider;
        user.providerId = providerId;
        user.password = "--------";
        user.wannaCheckFanTeam = true;
        return user;
    }

    public static User updateExistingUser(User existingUser, String nickname, String profileImg) {
//        existingUser.nickname = nickname;   // 닉네임은 개별적으로 유지되게
        existingUser.profileImg = profileImg;
        return existingUser;
    }

    public static User registerFanTeam(User user, String fanTeam) {
        user.fanTeam = fanTeam;
        return user;
    }

    public void update(UserUpdateRequest request) {
        this.nickname = request.nickname();
        this.profileImg = request.profileImge();
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void noWannaCheckFanTeam() {
        this.wannaCheckFanTeam = false;
    }

    public void wannaCheckFanTeam() {
        this.wannaCheckFanTeam = true;
    }
}
