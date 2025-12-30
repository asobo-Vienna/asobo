package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordFormatException extends RuntimeException {
    private final List<String> violations;

    public InvalidPasswordFormatException(String message) {
        super(message);
        this.violations = new ArrayList<>();
        this.violations.add(message);
    }

    public InvalidPasswordFormatException(String message, List<String> violations) {
        super(message);
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}
