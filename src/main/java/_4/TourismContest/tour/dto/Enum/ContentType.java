package _4.TourismContest.tour.dto.Enum;

public enum ContentType {
    맛집(39),
    숙소(32),
    쇼핑(38),
    문화(14);

    private final int value;

    ContentType(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}