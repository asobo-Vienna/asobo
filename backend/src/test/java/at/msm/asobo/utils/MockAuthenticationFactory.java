package at.msm.asobo.utils;

import at.msm.asobo.security.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;
import java.util.UUID;

public final class MockAuthenticationFactory {

    public static Authentication mockAuth(UUID userId, String username, String email, String... roles) {
        List<GrantedAuthority> authorities =
                AuthorityUtils.createAuthorityList(roles);

        UserPrincipal principal =
                new UserPrincipal(userId, username, email, authorities);

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

}
