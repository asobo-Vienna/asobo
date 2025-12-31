package at.msm.asobo.services;

import at.msm.asobo.security.PasswordValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.passwordValidator = new PasswordValidator();
    }

    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public boolean matches(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    public void validatePasswordFormat(String password) {
        this.passwordValidator.validate(password);
    }
}

