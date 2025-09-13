package at.msm.asobo.controllers;

import at.msm.asobo.dto.token.TokenDTO;
import at.msm.asobo.dto.token.TokenRequestDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserLoginDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.services.AuthService;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /*@PostMapping("/token")
    public TokenDTO token(@RequestBody @Valid TokenRequestDTO tokenRequest) {
        return authService.createToken(tokenRequest);
    }*/

    @PostMapping("/register")
    public TokenDTO register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        UserDTO userDTO = this.userService.registerUser(userRegisterDTO);

        // return accessToken
        return this.authService.createToken(new TokenRequestDTO(userDTO.getId(), userDTO.getUsername(), userDTO.getPassword()));
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        this.userService.loginUser(userLoginDTO);

        //this.authService
        return new TokenDTO();
    }
}
