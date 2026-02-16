package at.msm.asobo.config;

import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.RestAuthenticationEntryPoint;
import at.msm.asobo.security.TokenAuthenticationFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final TokenAuthenticationFilter tokenAuthenticationFilter;
  private final CustomUserDetailsService customUserDetailsService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  public SecurityConfig(
      TokenAuthenticationFilter tokenAuthenticationFilter,
      CustomUserDetailsService customUserDetailsService,
      RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
    this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    this.customUserDetailsService = customUserDetailsService;
    this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    http.cors(
        cors ->
            cors.configurationSource(
                request -> {
                  CorsConfiguration config = new CorsConfiguration();
                  config.setAllowedOrigins(
                      List.of(
                          "http://localhost:4200" // frontend
                          ));
                  config.setAllowedMethods(
                      List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                  config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
                  config.setAllowCredentials(
                      false); // if set to true: allow cookies or auth headers
                  return config;
                }));

    // CSRF (cross-site request forgery) protection is not needed, because application uses
    // stateless
    // JWT-based authentication and not cookie-based sessions.
    http.csrf(AbstractHttpConfigurer::disable);

    http.sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // disables Spring Security's default form-based login.
    http.formLogin(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(
        registry ->
            registry
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/assets/**",
                    "/images/**",
                    "/media/**",
                    "**.js",
                    "**.css",
                    "**.png",
                    "**.jpg",
                    "**.svg",
                    "**.ico")
                .permitAll()
                .requestMatchers("/api/roles/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/auth/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/events/**")
                .permitAll()
                .requestMatchers("/api/users/countries")
                .permitAll()
                .requestMatchers("/api/users/**")
                .hasAnyRole("USER", "ADMIN", "SUPERADMIN")
                .requestMatchers("/api/search")
                .permitAll()
                .requestMatchers("/api/admin/**")
                .hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/uploads/**")
                .permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                .anyRequest()
                .authenticated());

    // throws HTTP Error 401 with message 'Invalid credentials or authentication required' if
    // token is expired/invalid/missing
    http.exceptionHandling(
        exception -> exception.authenticationEntryPoint(restAuthenticationEntryPoint));

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder);
    provider.setUserDetailsService(customUserDetailsService);

    return new ProviderManager(provider);
  }
}
