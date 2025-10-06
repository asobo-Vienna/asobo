package at.msm.asobo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import at.msm.asobo.security.TokenAuthenticationFilter;
import at.msm.asobo.security.CustomUserDetailsService;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            TokenAuthenticationFilter tokenAuthenticationFilter,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http.addFilterBefore(
                tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of(
                    "http://localhost:4200", // your frontend
                    "http://localhost:63342",
                    "http://localhost:63343"
            ));
            config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
            config.setAllowCredentials(true); // allow cookies or auth headers
            return config;
        }));

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(
                conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.formLogin(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(registry -> registry
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/events/**").permitAll()
                .requestMatchers("/**").permitAll()
                //.requestMatchers("/api/**").permitAll() // TODO: remove later, otherwise all api endpoints are open
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/users/**").authenticated()
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(customUserDetailsService);

        return new ProviderManager(provider);
    }
}
