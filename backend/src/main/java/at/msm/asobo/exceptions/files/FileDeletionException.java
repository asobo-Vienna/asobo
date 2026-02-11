package at.msm.asobo.exceptions.files;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileDeletionException extends RuntimeException {
  public FileDeletionException(String message) {
    super(message);
  }
}
