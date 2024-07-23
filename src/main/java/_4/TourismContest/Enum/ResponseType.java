package _4.TourismContest.Enum;

public enum ResponseType {
    SUCCESS(200),
    FAILURE(400);

    private final int code;

    ResponseType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.name();
    }

}
