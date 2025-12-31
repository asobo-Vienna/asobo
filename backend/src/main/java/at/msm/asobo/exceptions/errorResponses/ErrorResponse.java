package at.msm.asobo.exceptions.errorResponses;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {
    private final String code;
    private final String message;
    private final int statusCode;
    private final List<String> violations;

    public ErrorResponse(String code, String message, int statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.violations = new ArrayList<>();
    }

    public ErrorResponse(String code, String message, List<String> violations, int statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.violations = violations;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public int getStatusCode() { return this.statusCode; }

    public List<String> getViolations() {
        return this.violations;
    }
}