package _4.TourismContest.tour.dto.detailIntroResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
@Getter
public class TourApiRestaurantDetailIntroResponseDto implements TourApiDetailIntroResponseDto {
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
        @JsonProperty("infocenterfood")
        private String infocenterfood;
        @JsonProperty("opentimefood")
        private String opentimefood;
        @JsonProperty("restdatefood")
        private String restdatefood;
        @JsonProperty("packing")
        private String packing;
        @JsonProperty("firstmenu")
        private String firstmenu;
        @JsonProperty("treatmenu")
        private String treatmenu;
    }
}
