package at.msm.asobo.services;

import at.msm.asobo.dto.token.TokenDTO;
import at.msm.asobo.dto.token.TokenRequestDTO;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public TokenDTO createToken(TokenRequestDTO tokenRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    tokenRequestDTO.getUsername(),
                    tokenRequestDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // real JWT here
        TokenDTO token = new TokenDTO(); // implement mapper?
        token.setAccessToken(this.jwtUtil.generateToken(userPrincipal.getUsername()));
        return token;
    }
}
