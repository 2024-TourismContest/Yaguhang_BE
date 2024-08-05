package _4.TourismContest.tour.dto.detailIntroResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TourApiCultureDetailIntroResponseDto implements TourApiDetailIntroResponseDto {
    private Response response;
    @Getter
    public static class Response {
        @JsonProperty("body")
        private Body body;
    }
    @Getter
    public static class Body {
        @JsonProperty("items")
        private Items items;
        private int totalCount;
    }
    @Getter
    public static class Items {
        @JsonProperty("item")
        private List<Item> item;
    }
    @Getter
    public static class Item {
        @JsonProperty("infocenterculture")
        private String infocenterculture;
        @JsonProperty("usetimeculture")
        private String usetimeculture;
        @JsonProperty("restdateculture")
        private String restdateculture;
        @JsonProperty("parkingculture")
        private String parkingculture;
        @JsonProperty("chkpetculture")
        private String chkpetculture;
    }
}
