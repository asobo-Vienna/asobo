package at.msm.asobo.controllers;

import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.mappers.LoginResponseDTOToUserPublicDTOMapper;
import at.msm.asobo.mappers.UserDTOToUserPublicDTOMapper;
import at.msm.asobo.security.*;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

        String expectedJson = objectMapper.writeValueAsString(expectedUser);

        mockMvc.perform(get("/api/users/id/{id}", expectedUser.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
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

        verify(userService).getUserDTOById(any(UUID.class));
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

        String expectedJson = objectMapper.writeValueAsString(expectedUser);

        mockMvc.perform(get("/api/users/{username}", "testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
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

        verify(userService).getUserByUsername(username);
    }

//    @Test
//    void updateUser_allFieldsSameUser_returnsExpectedResult() throws Exception {
//        UUID testId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
//
//        UserPrincipal userPrincipal = mock(UserPrincipal.class);
//        when(userPrincipal.getUserId()).thenReturn(testId);
//        when(userPrincipal.getUsername()).thenReturn("testuser");
//
//        UserUpdateDTO updateDTO = new UserTestBuilder()
//                .withId(testId)
//                .withUsername("testuser")
//                .withEmail("updated@example.com")
//                .withFirstName("Updated")
//                .withSurname("Name")
//                .withSalutation("Dr.")
//                .withPassword("Update123!")
//                .buildUserUpdateDTO();
//
//        LoginResponseDTO expectedUser = new UserTestBuilder()
//                .withId(testId)
//                .withUsername("testuser")
//                .withEmail("updated@example.com")
//                .withFirstName("Updated")
//                .withSurname("Name")
//                .withSalutation("Dr.")
//                .buildLoginResponseDTO();
//
//        when(userService.updateUserById(eq(testId), eq(testId), any(UserUpdateDTO.class)))
//                .thenReturn(expectedUser);
//
//        String expectedJson = objectMapper.writeValueAsString(expectedUser);
//
//        mockMvc.perform(patch("/api/users/{id}", testId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDTO))
//                        .with(csrf())
//                        .with(authentication(new UsernamePasswordAuthenticationToken(
//                                userPrincipal, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json(expectedJson));
//    }
}