package _4.TourismContest.spot.presentation;

import _4.TourismContest.oauth.application.CurrentUser;
import _4.TourismContest.oauth.application.UserPrincipal;
import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.spot.dto.event.SpotStadiumPreviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stadium")
@RequiredArgsConstructor
public class SpotStadiumController {
    private final SpotService spotService;

    @GetMapping("/{stadium}/{category}/{pagesize}/{pageindex}")
    public ResponseEntity<SpotStadiumPreviewResponse> getStadiumSpot(@PathVariable String stadium, @PathVariable String category,
                                                                     @PathVariable Integer pagesize, @PathVariable Integer pageindex, @CurrentUser UserPrincipal userPrincipal) {
        SpotStadiumPreviewResponse spotStadiumPreviewResponse = spotService.getStadiumSpot(stadium, category, pagesize, pageindex, userPrincipal);
        return new ResponseEntity<>(spotStadiumPreviewResponse, HttpStatus.OK);
    }
}
