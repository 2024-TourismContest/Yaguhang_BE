package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class SpotMainController {
    private final SpotService spotService;

    @GetMapping("/place/{stadium}/{category}")
    public ResponseEntity<SpotCategoryResponse> getMainSpot(@PathVariable String stadium, @PathVariable String category, @CurrentUser UserPrincipal userPrincipal) {
        SpotCategoryResponse spotCategoryResponse = spotService.getMainSpot(stadium, category, userPrincipal);
        return new ResponseEntity<>(spotCategoryResponse, HttpStatus.OK);
    }

}
