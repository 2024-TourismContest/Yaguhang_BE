package _4.TourismContest.tour.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
@Getter
public class TourApiResponseDto {
    private Response response;
    @Getter
    public class Response {
        @JsonProperty("body")
        private Body body;
    }
    @Getter
    public class Body {
        @JsonProperty("items")
        private Items items;
    }
    @Getter
    public class Items {
        @JsonProperty("item")
        private List<Item> item;
    }
    @Getter
    public class Item {
        @JsonProperty("addr1")
        private String addr1;
        @JsonProperty("addr2")
        private String addr2;
        @JsonProperty("booktour")
        private String booktour;
        @JsonProperty("contentid")
        private String contentid;
        @JsonProperty("firstimage")
        private String firstimage;
        @JsonProperty("firstimage2")
        private String firstimage2;
        @JsonProperty("mapx")
        private String mapx;
        @JsonProperty("mapy")
        private String mapy;
        @JsonProperty("mlevel")
        private String mlevel;
        @JsonProperty("tel")
        private String tel;
        @JsonProperty("title")
        private String title;
    }
}
