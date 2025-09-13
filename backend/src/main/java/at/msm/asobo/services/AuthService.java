package at.msm.asobo.services;

import at.msm.asobo.dto.token.TokenDTO;
import at.msm.asobo.dto.token.TokenRequestDTO;
import at.msm.asobo.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public TokenDTO createToken(TokenRequestDTO tokenRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    tokenRequest.getUsername(),
                    tokenRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // real JWT here
        TokenDTO token = new TokenDTO();
        token.setAccessToken(userPrincipal.getUserId());
        return token;
    }
}
