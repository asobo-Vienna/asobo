package at.msm.asobo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class MediumNotFoundException extends RuntimeException {

    public MediumNotFoundException(UUID id) {
        super("Could not find medium with ID " + id);
    }

    public MediumNotFoundException(String message){
        super (message);
    }
}
