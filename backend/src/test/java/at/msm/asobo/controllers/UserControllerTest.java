package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.exceptions.UserNotFoundException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

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
        // Configure the mocked filter to pass requests through using the public doFilter method
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

    static class UserTestBuilder {
        private UUID id = UUID.randomUUID();
        private String username = "testuser";
        private String email = "test@example.com";
        private String firstName = "Test";
        private String surname = "User";
        private String password = "password";

        public UserTestBuilder withoutId() {
            return this;
        }

        public UserTestBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public UserTestBuilder withUsername(String username) {
            this.username = username;
            this.email = username + "@example.com";
            return this;
        }

        public UserTestBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserTestBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserTestBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public UserTestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserPublicDTO buildUserPublicDTO() {
            UserPublicDTO user = new UserPublicDTO();
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setSurname(surname);
            return user;
        }

        public UserRegisterDTO buildUserRegisterDTO() {
            UserRegisterDTO user = new UserRegisterDTO();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setSurname(surname);
            return user;
        }
    }

    private UserPublicDTO createDefaultTestUser() {
        return new UserTestBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withUsername("testuser")
                .withEmail("test@example.com")
                .buildUserPublicDTO();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserById_returnsExpectedResult() throws Exception {
        UserPublicDTO expectedUser = createDefaultTestUser();

        when(userService.getUserDTOById(expectedUser.getId())).thenReturn(expectedUser);

        mockMvc.perform(get("/api/users/id/{id}", expectedUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.getId().toString()))
                .andExpect(jsonPath("$.username").value(expectedUser.getUsername()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.getFirstName()))
                .andExpect(jsonPath("$.surname").value(expectedUser.getSurname()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserById_whenUserNotFound_returns404() throws Exception {
        UUID userId = UUID.randomUUID();

        when(userService.getUserDTOById(any(UUID.class)))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/users/id/{id}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserByUsername_returnsExpectedResult() throws Exception {
        UUID testId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        UserPublicDTO expectedUser = new UserTestBuilder()
                .withId(testId)
                .withUsername("testuser")
                .buildUserPublicDTO();

        when(userService.getUserByUsername("testuser")).thenReturn(expectedUser);

        mockMvc.perform(get("/api/users/{username}", "testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.getId().toString()))
                .andExpect(jsonPath("$.username").value(expectedUser.getUsername()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.getFirstName()))
                .andExpect(jsonPath("$.surname").value(expectedUser.getSurname()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getUserByUsername_whenUserNotFound_returns404() throws Exception {
        String username = "testuser";

        when(userService.getUserByUsername(username))
                .thenThrow(new UserNotFoundException("username"));

        mockMvc.perform(get("/api/users/{username}", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}