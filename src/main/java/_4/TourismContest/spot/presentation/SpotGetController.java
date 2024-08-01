package _4.TourismContest.spot.presentation;

import _4.TourismContest.spot.application.SpotService;
import _4.TourismContest.spot.dto.event.SpotCategoryResponse;
import _4.TourismContest.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class SpotGetController {
    private final SpotService spotService;

    @GetMapping("/place/{stadium}/{category}")
    public ResponseEntity<SpotCategoryResponse> getMainSpot(@PathVariable String stadium, @PathVariable String category) {

        return new ResponseEntity<>(spotService.getMainSpot(stadium, category), HttpStatus.OK);
    }

}
