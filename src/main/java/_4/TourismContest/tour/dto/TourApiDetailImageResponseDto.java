    package _4.TourismContest.tour.dto;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
        public static class Items {
            @JsonProperty("item")
            private List<Item> item;

            public void setItem(List<Item> item) {
                this.item = item;
            }
        }
        @Getter
        public static class Item {
            @JsonProperty("originimgurl")
            private String originimgurl;
        }
    }
