package at.msm.asobo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService customUserDetailsService;

  // List of public endpoints that don't require authentication
  private static final List<String> PUBLIC_ENDPOINTS =
      List.of("/api/auth/", "/api/search", "/uploads/");

  public TokenAuthenticationFilter(
      JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
    this.jwtUtil = jwtUtil;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    System.out.println(">>> TokenAuthenticationFilter: " + request.getMethod() + " " + requestURI);

    // Check if this is a public endpoint
    boolean isPublicEndpoint = PUBLIC_ENDPOINTS.stream().anyMatch(requestURI::startsWith);

    if (isPublicEndpoint) {
      System.out.println(">>> Skipping JWT check for: " + requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (jwtUtil.validateToken(token)) {
        UUID userId = UUID.fromString(jwtUtil.getUserIdFromToken(token));

        UserPrincipal userPrincipal =
            (UserPrincipal) this.customUserDetailsService.loadUserById(userId);

        UserPrincipalAuthenticationToken authentication =
            new UserPrincipalAuthenticationToken(userPrincipal);

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
