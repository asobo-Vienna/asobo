package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.AvailabilityDTO;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.auth.UserLoginDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.services.AuthService;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        return this.authService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        System.out.println(">>> Login request: " + userLoginDTO.getIdentifier());
        return this.authService.loginUser(userLoginDTO);
    }

    @GetMapping("/check-username")
    public AvailabilityDTO checkUsernameAvailability(@RequestParam String username) {
        return new AvailabilityDTO(!this.userService.isUsernameAlreadyTaken(username));
    }

    @GetMapping("/check-email")
    public AvailabilityDTO checkEmailAvailability(@RequestParam String email) {
        return new AvailabilityDTO(!this.userService.isEmailAlreadyTaken(email));
    }
}
