package at.msm.asobo.controllers;

import at.msm.asobo.dto.token.TokenDTO;
import at.msm.asobo.dto.token.TokenRequestDTO;
import at.msm.asobo.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public TokenDTO token(@RequestBody @Valid TokenRequestDTO tokenRequest) {
        return authService.createToken(tokenRequest);
    }
}
