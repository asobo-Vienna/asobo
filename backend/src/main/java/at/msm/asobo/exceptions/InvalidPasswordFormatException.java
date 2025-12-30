package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidPasswordFormatException extends RuntimeException {
    public InvalidPasswordFormatException() {
        super("Password has an invalid format!");
    }

    public InvalidPasswordFormatException(String message) {
        super(message);
    }
}
