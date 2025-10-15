package at.msm.asobo.exceptions;

import at.msm.asobo.exceptions.registration.EmailAlreadyExistsException;
import at.msm.asobo.exceptions.registration.UsernameAlreadyExistsException;
import at.msm.asobo.exceptions.errorResponses.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMsg);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("EMAIL_EXISTS", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameExists(UsernameAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse("USERNAME_EXISTS", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = ex.getMessage();

        if (message != null && message.contains("users_username_key")) {
            return new ResponseEntity<>(
                    new ErrorResponse("USERNAME_EXISTS", "This username is already taken"),
                    HttpStatus.CONFLICT
            );
        }

        if (message != null && message.contains("users_email_key")) {
            return new ResponseEntity<>(
                    new ErrorResponse("EMAIL_EXISTS", "This email is already registered"),
                    HttpStatus.CONFLICT
            );
        }

        return new ResponseEntity<>(
                new ErrorResponse("DATABASE_ERROR", "A database constraint was violated"),
                HttpStatus.CONFLICT
        );
    }
}