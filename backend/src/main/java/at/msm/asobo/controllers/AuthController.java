package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.auth.UserLoginDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO register(@ModelAttribute @Valid UserRegisterDTO userRegisterDTO) {
        return this.userService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        System.out.println(">>> Login request: " + userLoginDTO.getIdentifier());
        return this.userService.loginUser(userLoginDTO);
    }

    @GetMapping("/check-username/{username}")
    public boolean checkUsernameAvailability(@PathVariable String username) {
        return !this.userService.isUsernameAlreadyTaken(username);
    }

    @GetMapping("/check-email/{email}")
    public boolean checkEmailAvailability(@PathVariable String email) {
        return !this.userService.isEmailAlreadyTaken(email);
    }
}
