package _4.TourismContest.user.dto.event;

import _4.TourismContest.user.domain.User;
import lombok.Builder;

import java.util.Map;

@Builder
public record UserInfoDto(
        Long userId,
        String nickname,
        String image,
        String fanTeam
) {

    public static UserInfoDto of(User user) {
        Map<String, String> teamLogoMap = Map.of(
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
        String baseUrl = "https://yaguhang.kro.kr:8443/teamLogos/";
        String fanTeam;
        if(user.getFanTeam() == null || user.getFanTeam().equals(""))
            fanTeam = baseUrl + "main.svg";
        else{
            String logoFileName = teamLogoMap.get(user.getFanTeam());

            if (logoFileName == null) {
                throw new IllegalArgumentException("Unknown team: " + user.getFanTeam() + ". Please check the team name.");
            }

            fanTeam = baseUrl + logoFileName;
        }


        return new UserInfoDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImg(),
                fanTeam
        );
    }
}
