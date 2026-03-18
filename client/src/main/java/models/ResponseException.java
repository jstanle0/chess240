package models;

public class ResponseException extends Exception {
    private final Integer responseCode;

    public ResponseException(Exception e, Integer code) {
        super(e.getMessage());
        responseCode = code;
    }

    public ResponseException(String s, Integer code) {
        super(s);
        responseCode = code;
    }

    public Integer getCode() {
        return responseCode;
    }
}
