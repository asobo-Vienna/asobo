package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.comment.UserCommentDTO;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserCommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCommentController.class)
@EnableMethodSecurity
class UserCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private UserCommentService userCommentService;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UUID eventId;
    private UUID commentId;
    private UserCommentDTO userCommentDTO1;
    private UserCommentDTO userCommentDTO2;
    private final String ALL_COMMENTS_URL = "/api/events/{eventId}/comments";
    private final String SINGLE_COMMENT_URL =  "/api/events/{eventId}/comments/{commentId}";

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        userCommentDTO1 = new UserCommentDTO();
        userCommentDTO1.setId(commentId);
        userCommentDTO1.setText("Test comment #1!");

        userCommentDTO2 = new UserCommentDTO();
        userCommentDTO2.setId(commentId);
        userCommentDTO2.setText("Test comment #2, yo!");
    }

    private UserPrincipal createUserPrincipal(String role) {
        return new UserPrincipal(
                UUID.randomUUID(),
                "username",
                "test@example.com",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
    void getAllUserCommentsByEventId_withAuthorizedRole_returns200(String role) throws Exception {
        List<UserCommentDTO> comments = List.of(userCommentDTO1, userCommentDTO2);
        String expectedJson = objectMapper.writeValueAsString(comments);

        when(userCommentService.getUserCommentsByEventId(eventId)).thenReturn(comments);

        mockMvc.perform(get(ALL_COMMENTS_URL, eventId)
                        .with(user("testuser").roles(role)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(userCommentService).getUserCommentsByEventId(eventId);
    }

    @Test
    @WithMockUser(roles = "X")
    void getAllUserCommentsByEventId_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get(ALL_COMMENTS_URL, eventId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @Test
    void getAllUserCommentsByEventId_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(ALL_COMMENTS_URL, eventId))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userCommentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
    void getUserCommentById_withAuthorizedRole_returns200(String role) throws Exception {
        String expectedJson = objectMapper.writeValueAsString(userCommentDTO1);

        when(userCommentService.getUserCommentByEventIdAndCommentId(eventId, commentId))
                .thenReturn(userCommentDTO1);

        mockMvc.perform(get(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(user("testuser").roles(role)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(userCommentService).getUserCommentByEventIdAndCommentId(eventId, commentId);
    }

    @Test
    @WithMockUser(roles = "X")
    void getUserCommentById_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get(SINGLE_COMMENT_URL, eventId, commentId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @Test
    void getUserCommentById_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(SINGLE_COMMENT_URL, eventId, commentId))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userCommentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
    void addNewComment_withAuthorizedRole_returns201(String role) throws Exception {
        String jsonComment = objectMapper.writeValueAsString(userCommentDTO1);

        when(userCommentService.addNewUserCommentToEventById(eq(eventId), any(UserCommentDTO.class)))
                .thenReturn(userCommentDTO1);

        mockMvc.perform(post(ALL_COMMENTS_URL, eventId)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonComment))
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonComment));

        verify(userCommentService).addNewUserCommentToEventById(eq(eventId), any(UserCommentDTO.class));
    }

    @Test
    @WithMockUser(roles = "X")
    void addNewComment_withUnauthorizedRole_returns403() throws Exception {
        String jsonComment =  objectMapper.writeValueAsString(userCommentDTO1);
        mockMvc.perform(post(ALL_COMMENTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonComment))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @Test
    void addNewComment_unauthenticated_returns401() throws Exception {
        String jsonComment =  objectMapper.writeValueAsString(userCommentDTO1);
        mockMvc.perform(post(ALL_COMMENTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonComment))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userCommentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewComment_withInvalidRequestBody_returns400() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post(ALL_COMMENTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userCommentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewComment_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post(ALL_COMMENTS_URL, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCommentDTO1)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
    void updateUserComment_withAuthorizedRole_returns200(String role) throws Exception {
        String jsonCommentRequest =  objectMapper.writeValueAsString(userCommentDTO1);
        String jsonCommentResponse =  objectMapper.writeValueAsString(userCommentDTO2);

        when(userCommentService.updateUserCommentByEventIdAndCommentId(
                eq(eventId), eq(commentId), any(UserCommentDTO.class), any(UserPrincipal.class)))
                .thenReturn(userCommentDTO2);

        UserPrincipal loggedInUser = createUserPrincipal(role);

        mockMvc.perform(put(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(user(loggedInUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCommentRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonCommentResponse));

        verify(userCommentService).updateUserCommentByEventIdAndCommentId(
                eq(eventId), eq(commentId), any(UserCommentDTO.class), any(UserPrincipal.class));
    }

    @Test
    @WithMockUser(roles = "X")
    void updateUserComment_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(put(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCommentDTO1)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @Test
    void updateUserComment_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCommentDTO1)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userCommentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserComment_withInvalidRequestBody_returns400() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(put(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userCommentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUserComment_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(put(SINGLE_COMMENT_URL, eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCommentDTO1)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
    void deleteUserComment_withAuthorizedRole_returns200(String role) throws Exception {
        when(userCommentService.deleteUserCommentByEventIdAndCommentId(
                eq(eventId), eq(commentId), any(UserPrincipal.class)))
                .thenReturn(userCommentDTO1);

        UserPrincipal loggedInUser = createUserPrincipal(role);

        mockMvc.perform(delete(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(user(loggedInUser))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userCommentDTO1)));

        verify(userCommentService).deleteUserCommentByEventIdAndCommentId(
                eq(eventId), eq(commentId), any(UserPrincipal.class));
    }

    @Test
    @WithMockUser(roles = "X")
    void deleteUserComment_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(delete(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }

    @Test
    void deleteUserComment_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete(SINGLE_COMMENT_URL, eventId, commentId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userCommentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUserComment_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(delete(SINGLE_COMMENT_URL, eventId, commentId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userCommentService);
    }
}