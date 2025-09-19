package at.msm.asobo.controllers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserLoginDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public LoginResponseDTO register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        return this.userService.registerUser(userRegisterDTO);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        System.out.println(">>> Login request: " + userLoginDTO.getIdentifier());
        return this.userService.loginUser(userLoginDTO);
    }
}
