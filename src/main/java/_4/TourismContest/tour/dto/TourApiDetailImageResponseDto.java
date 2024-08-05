    package _4.TourismContest.tour.dto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.List;
    @Getter
    public class TourApiDetailImageResponseDto {
        private Response response;
        @Getter
        public static class Response {
            @JsonProperty("body")
            private Body body;
        }
        @Getter
        public static class Body {
            @JsonProperty("items")
            @JsonDeserialize(using = ItemsDeserializer.class)
            private Items items;
            private int totalCount;
        }

        @Getter
        @Setter
        public static class Items {
            @JsonProperty("item")
            private List<Item> item;

        }
        @Getter
        @AllArgsConstructor
        public static class Item {
            @JsonProperty("originimgurl")
            private String originimgurl;
        }
    }
