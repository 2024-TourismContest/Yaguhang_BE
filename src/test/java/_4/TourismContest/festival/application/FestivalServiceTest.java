package _4.TourismContest.festival.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class FestivalServiceTest {

    @Autowired
    private FestivalService festivalService;

    @Value("${API.festival.key}")
    private String apiKey;

    private String eventStartDate = "20240504";
    private String eventEndDate = "20240901";

    @Test
    public void testUpdateFestivalList() {
        festivalService.updateFestivalList(eventStartDate, eventEndDate);
    }
}