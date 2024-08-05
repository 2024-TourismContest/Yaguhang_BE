package _4.TourismContest.tour.dto.detailIntroResponse;

import _4.TourismContest.tour.dto.detailIntroResponse.TourApiDetailIntroResponseDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
@Getter
public class TourApiShoppingDetailIntroResponseDto implements TourApiDetailIntroResponseDto {
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
        @JsonProperty("infocentershopping")
        private String infocentershopping;
        @JsonProperty("opentime")
        private String opentime;
        @JsonProperty("restdateshopping")
        private String restdateshopping;
        @JsonProperty("saleitem")
        private String saleitem;
        @JsonProperty("chkpetshopping")
        private String chkpetshopping;  // 애완동물
        @JsonProperty("parkingshopping")
        private String parkingshopping;

    }
}
