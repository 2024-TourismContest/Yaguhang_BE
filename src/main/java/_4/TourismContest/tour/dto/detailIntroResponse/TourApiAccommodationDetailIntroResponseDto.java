package _4.TourismContest.tour.dto.detailIntroResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
@Getter
public class TourApiAccommodationDetailIntroResponseDto implements TourApiDetailIntroResponseDto {
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
        @JsonProperty("infocenterlodging")
        private String infocenterlodging;
        @JsonProperty("checkintime")
        private String checkintime;
        @JsonProperty("checkouttime")
        private String checkouttime ;
        @JsonProperty("roomcount")
        private String roomcount;
        @JsonProperty("parkinglodging")
        private String parkinglodging;

    }
}
