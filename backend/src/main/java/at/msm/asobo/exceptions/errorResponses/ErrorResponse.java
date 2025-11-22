package at.msm.asobo.exceptions.errorResponses;

public class ErrorResponse {
    private String code;
    private String message;
    private int statusCode;

    public ErrorResponse(String code, String message, int statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public int getStatusCode() { return this.statusCode; }
}