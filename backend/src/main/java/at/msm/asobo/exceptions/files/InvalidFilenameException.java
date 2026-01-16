package at.msm.asobo.exceptions.files;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFilenameException extends RuntimeException {
    public InvalidFilenameException(String message) {
        super(message);
    }
}
