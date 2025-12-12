package at.msm.asobo.controllers;

import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.mappers.LoginResponseDTOToUserPublicDTOMapper;
import at.msm.asobo.mappers.UserDTOToUserPublicDTOMapper;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.RestAuthenticationEntryPoint;
import at.msm.asobo.security.TokenAuthenticationFilter;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

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

        when(userService.registerUser(any(UserRegisterDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(multipart("/api/auth/register")
                        .param("username", registerDTO.getUsername())
                        .param("email", registerDTO.getEmail())
                        .param("password", registerDTO.getPassword())
                        .param("firstName", registerDTO.getFirstName())
                        .param("surname", registerDTO.getSurname())
                        .param("salutation", registerDTO.getSalutation())
                        .with(csrf())
                        .with(user("anonymousUser"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.username").exists())
                .andExpect(jsonPath("$.user.email").exists());

        verify(userService).registerUser(any(UserRegisterDTO.class));
    }
}
