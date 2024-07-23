package _4.TourismContest.user.domain;

import _4.TourismContest.festival.domain.FestivalScrap;
import _4.TourismContest.oauth.domain.AuthProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FestivalScrap> festivalScrapList = new ArrayList<>();


    @Builder
    public User(String email, String password, String nickname, String profileImg) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public static User createOAuthUser(String email, String nickname, String profileImg, AuthProvider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.profileImg = profileImg;
        user.provider = provider;
        user.providerId = providerId;
        user.password = "--------";
        return user;
    }

    public static User updateExistingUser(User existingUser, String nickname, String profileImg){
//        existingUser.nickname = nickname;   // 닉네임은 개별적으로 유지되게
        existingUser.profileImg = profileImg;
        return  existingUser;
    }
}