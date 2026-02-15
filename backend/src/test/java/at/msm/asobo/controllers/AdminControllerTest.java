package at.msm.asobo.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.filter.MediumFilterDTO;
import at.msm.asobo.dto.filter.UserCommentFilterDTO;
import at.msm.asobo.dto.filter.UserFilterDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserAdminSummaryDTO;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.services.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@EnableMethodSecurity
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AdminService adminService;

  @MockitoBean private FileStorageProperties fileStorageProperties;

  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private CustomUserDetailsService customUserDetailsService;

  private static final String USERS_URL = "/api/admin/users";
  private static final String USERS_PAGINATED_URL = "/api/admin/users/paginated";
  private static final String COMMENTS_URL = "/api/admin/comments";
  private static final String MEDIA_URL = "/api/admin/media";

  @ParameterizedTest
  @ValueSource(strings = {USERS_URL, COMMENTS_URL, MEDIA_URL})
  void getAllUsers_Paginated_WithoutAuthentication_ReturnsForbidden(String url) throws Exception {
    mockMvc.perform(get(url)).andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(strings = {USERS_URL, COMMENTS_URL, MEDIA_URL})
  @WithMockUser(roles = "USER")
  void getAllUsers_Paginated_WithUserRole_ReturnsForbidden(String url) throws Exception {
    mockMvc.perform(get(url)).andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
  void getAllUsers_WithAdminRole_ReturnsPageOfUsersPaginated(String role) throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    UserAdminSummaryDTO user1 = new UserAdminSummaryDTO();
    user1.setId(UUID.randomUUID());

    UserAdminSummaryDTO user2 = new UserAdminSummaryDTO();
    user2.setId(UUID.randomUUID());

    Page<UserAdminSummaryDTO> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);
    String expectedJson = objectMapper.writeValueAsString(userPage);

    when(adminService.getAllUsersPaginated(any(UserFilterDTO.class), any(Pageable.class)))
        .thenReturn(userPage);

    mockMvc
        .perform(
            get(USERS_PAGINATED_URL)
                .with(user("testadmin").roles(role))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(adminService).getAllUsersPaginated(any(UserFilterDTO.class), any(Pageable.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
  void getAllComments_WithAdminRole_ReturnsPageOfComments(String role) throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    UserCommentWithEventTitleDTO comment1 = new UserCommentWithEventTitleDTO();
    UserCommentWithEventTitleDTO comment2 = new UserCommentWithEventTitleDTO();

    Page<UserCommentWithEventTitleDTO> commentPage =
        new PageImpl<>(List.of(comment1, comment2), pageable, 2);
    String expectedJson = objectMapper.writeValueAsString(commentPage);

    when(adminService.getAllUserCommentsWithEventTitle(
            any(UserCommentFilterDTO.class), any(Pageable.class)))
        .thenReturn(commentPage);

    mockMvc
        .perform(
            get(COMMENTS_URL)
                .with(user("testadmin").roles(role))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(adminService)
        .getAllUserCommentsWithEventTitle(any(UserCommentFilterDTO.class), any(Pageable.class));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
  void getAllMedia_WithAdminRole_ReturnsPageOfMedia(String role) throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    MediumWithEventTitleDTO media1 = new MediumWithEventTitleDTO();
    MediumWithEventTitleDTO media2 = new MediumWithEventTitleDTO();

    Page<MediumWithEventTitleDTO> mediaPage = new PageImpl<>(List.of(media1, media2), pageable, 2);
    String expectedJson = objectMapper.writeValueAsString(mediaPage);

    when(adminService.getAllMediaWithEventTitle(any(MediumFilterDTO.class), any(Pageable.class)))
        .thenReturn(mediaPage);

    mockMvc
        .perform(
            get(MEDIA_URL)
                .with(user("testadmin").roles(role))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(adminService).getAllMediaWithEventTitle(any(MediumFilterDTO.class), any(Pageable.class));
  }
}
