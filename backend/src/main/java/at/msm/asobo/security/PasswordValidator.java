package at.msm.asobo.security;

import at.msm.asobo.exceptions.InvalidPasswordFormatException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    // TODO: maybe move these into dedicated DB table
    private static final int MIN_PW_LENGTH = 6;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*(),.?:;{}|<>].*");

    public void validate(String password) {
        List<String> violations = new ArrayList<>();

        if (password == null) {
            throw new InvalidPasswordFormatException("Password must be non-null!");
        }

        if (password.length() < MIN_PW_LENGTH) {
            violations.add("Password must be at least " + MIN_PW_LENGTH + " characters long!");
        }

        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            violations.add("Password must contain at least one uppercase letter!");
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            violations.add("Password must contain at least one lowercase letter!");
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            violations.add("Password must contain at least one digit!");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            violations.add("Password must contain at least one special character!");
        }

        if (!violations.isEmpty()) {
            throw new InvalidPasswordFormatException(
                    "Password does not meet requirements!",
                    violations
            );
        }
    }
}
