package at.msm.asobo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        System.out.println(">>> TokenAuthenticationFilter: " + request.getMethod() + " " + requestURI);
        System.out.println(">>> TokenAuthenticationFilter: URI = " + requestURI);

        // Skip all /api/auth/** endpoints
        if (requestURI.startsWith("/api/auth/")) {
            System.out.println(">>> Skipping JWT check for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // get substring after "Bearer "
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                // extract info from token
                String username = jwtUtil.getUsernameFromToken(token);
                //String userId = jwtUtil.getUserIdFromToken(token);

                UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserByUsername(username);

                UserPrincipalAuthenticationToken authentication =
                        new UserPrincipalAuthenticationToken(userPrincipal);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }

        filterChain.doFilter(request, response);
    }
}
