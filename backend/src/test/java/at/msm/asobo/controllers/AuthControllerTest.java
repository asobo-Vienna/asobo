package at.msm.asobo.controllers;

import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.config.SecurityConfig;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.auth.UserLoginDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.mappers.LoginResponseDTOToUserPublicDTOMapper;
import at.msm.asobo.mappers.UserDTOToUserPublicDTOMapper;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.RestAuthenticationEntryPoint;
import at.msm.asobo.security.TokenAuthenticationFilter;
import at.msm.asobo.services.AuthService;
import at.msm.asobo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserDTOToUserPublicDTOMapper userDTOToUserPublicDTOMapper;

    @MockitoBean
    private LoginResponseDTOToUserPublicDTOMapper loginResponseDTOToUserPublicDTOMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    private final String REGISTER_URL = "/api/auth/register";
    private final String LOGIN_URL = "/api/auth/login";
    private final String CHECK_USERNAME_URL = "/api/auth/check-username/{username}";
    private final String CHECK_EMAIL_URL = "/api/auth/check-email/{email}";

    @BeforeEach
    void setUp() throws ServletException, IOException {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(tokenAuthenticationFilter).doFilter(
                any(ServletRequest.class),
                any(ServletResponse.class),
                any(FilterChain.class)
        );
    }

    @Test
    void registerUser_returnsExpectedResult() throws Exception {
        UserRegisterDTO registerDTO = new UserTestBuilder()
                .withoutId()
                .withUsername("testuser")
                .withEmail("testuser@example.com")
                .withFirstName("Test")
                .withSurname("User")
                .withPassword("password123")
                .withSalutation("Mr.")
                .buildUserRegisterDTO();

        UserPublicDTO expectedUser = new UserTestBuilder()
                .withUsername("testuser")
                .withEmail("test@example.com")
                .buildUserPublicDTO();

        LoginResponseDTO mockResponse = new LoginResponseDTO("any-token", expectedUser);

        when(authService.registerUser(any(UserRegisterDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post(REGISTER_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.email").exists());

        verify(authService).registerUser(any(UserRegisterDTO.class));
    }

    @Test
    void registerUserMissingRequiredFields_returns400() throws Exception {
        UserRegisterDTO invalidDTO = new UserRegisterDTO();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).registerUser(invalidDTO);
    }

    @Test
    void login_success() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("testuser", "password123");

        UserPublicDTO expectedUser = new UserTestBuilder()
                .withUsername("testuser")
                .buildUserPublicDTO();

        LoginResponseDTO mockResponse = new LoginResponseDTO("token-456", expectedUser);
        when(authService.loginUser(any(UserLoginDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post(LOGIN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-456"));

        verify(authService).loginUser(any(UserLoginDTO.class));
    }

    @Test
    void login_invalidCredentials() throws Exception {
        UserLoginDTO loginDTO = new UserLoginDTO("wronguser", "wrongpassword");

        when(authService.loginUser(any(UserLoginDTO.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());

        verify(authService).loginUser(any(UserLoginDTO.class));
    }

    @Test
    void checkUsernameAvailability_available() throws Exception {
        String username = "available";
        when(userService.isUsernameAlreadyTaken(username)).thenReturn(false);

        mockMvc.perform(get(CHECK_USERNAME_URL, username))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService).isUsernameAlreadyTaken(username);
    }

    @Test
    void checkUsernameAvailability_taken() throws Exception {
        String username = "taken";
        when(userService.isUsernameAlreadyTaken(username)).thenReturn(true);

        mockMvc.perform(get(CHECK_USERNAME_URL, username))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(userService).isUsernameAlreadyTaken(username);
    }

    @Test
    void checkEmailAvailability_available() throws Exception {
        String email = "new@example.com";
        when(userService.isEmailAlreadyTaken(email)).thenReturn(false);

        mockMvc.perform(get(CHECK_EMAIL_URL, email))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(userService).isEmailAlreadyTaken(email);
    }

    @Test
    void checkEmailAvailability_taken() throws Exception {
        String email = "taken@example.com";
        when(userService.isEmailAlreadyTaken(email)).thenReturn(true);

        mockMvc.perform(get(CHECK_EMAIL_URL, email))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(userService).isEmailAlreadyTaken(email);
    }
}
