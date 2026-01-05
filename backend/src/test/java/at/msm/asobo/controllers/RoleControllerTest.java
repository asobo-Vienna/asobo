package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.dto.user.UserRolesDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.services.RoleService;
import at.msm.asobo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@EnableMethodSecurity
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UUID userId;
    private RoleDTO testRole1;
    private RoleDTO testRole2;
    private User testUser;
    private List<RoleDTO> allRoles;
    private final String ROLES_URL = "/api/roles";
    private final String ASSIGN_URL = "/api/roles/assign";

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testRole1 = new RoleDTO();
        testRole1.setId(1L);
        testRole1.setName("TESTROLE");

        testRole2 = new RoleDTO();
        testRole2.setId(2L);
        testRole2.setName("TESTROLE_XY");

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRoles(new HashSet<>());

        allRoles = List.of(testRole1, testRole2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void getAllRoles_withAuthorizedRole_returns200(String role) throws Exception {

        when(roleService.getAllRoles()).thenReturn(allRoles);

        String expectedJson = objectMapper.writeValueAsString(allRoles);

        mockMvc.perform(get(ROLES_URL)
                        .with(user("testuser").roles(role)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(roleService).getAllRoles();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get(ROLES_URL))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roleService);
    }

    @Test
    void getAllRoles_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(ROLES_URL))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roleService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withAuthorizedRole_returns200(String role) throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        UserRolesDTO expectedResponse = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(roleService.assignRoles(userId, roles)).thenReturn(expectedResponse);

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignRole_withUnauthorizedRole_returns403() throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1, testRole2);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(patch(ASSIGN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roleService);
    }

    @Test
    void assignRole_unauthenticated_returns401() throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roleService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignRole_withoutCsrf_returns403() throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch(ASSIGN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roleService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withUserNotFound_returns404(String role) throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(roleService.assignRoles(userId, roles))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(roleService).assignRoles(userId, roles);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withRoleNotFound_returns404(String role) throws Exception {

        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO request = new UserRolesDTO(userId, roles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(roleService.assignRoles(userId, roles))
                .thenThrow(new RoleNotFoundException("Role not found: " + testRole1.getName()));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(roleService).assignRoles(userId, roles);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withMissingUserId_returns400(String role) throws Exception {
        Set<RoleDTO> roles = Set.of(testRole1);
        UserRolesDTO requestWithoutId = new UserRolesDTO();
        requestWithoutId.setRoles(roles);

        String jsonRequest = objectMapper.writeValueAsString(requestWithoutId);

        when(roleService.assignRoles(null, roles))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(roleService).assignRoles(null, roles);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withEmptyRoles_returns400(String role) throws Exception {
        UserRolesDTO requestWithoutRoles = new UserRolesDTO(userId, Collections.emptySet());
        String jsonRequest = objectMapper.writeValueAsString(requestWithoutRoles);

        when(roleService.assignRoles(userId, Collections.emptySet()))
                .thenThrow(new IllegalArgumentException("At least one role is required"));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(roleService).assignRoles(userId, Collections.emptySet());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withMissingUserRole_returns400(String role) throws Exception {
        Set<RoleDTO> rolesWithoutUser = Set.of(testRole1); // Only ADMIN, no USER
        UserRolesDTO request = new UserRolesDTO(userId, rolesWithoutUser);
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(roleService.assignRoles(userId, rolesWithoutUser))
                .thenThrow(new IllegalArgumentException("Every user requires the role USER"));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(roleService).assignRoles(userId, rolesWithoutUser);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withInvalidSuperAdminHierarchy_returns400(String role) throws Exception {
        RoleDTO superAdminRole = new RoleDTO();
        superAdminRole.setName("SUPERADMIN");

        Set<RoleDTO> invalidRoles = Set.of(superAdminRole); // SUPERADMIN without ADMIN/USER
        UserRolesDTO request = new UserRolesDTO(userId, invalidRoles);
        String jsonRequest = objectMapper.writeValueAsString(request);

        when(roleService.assignRoles(userId, invalidRoles))
                .thenThrow(new IllegalArgumentException("SUPERADMIN requires ADMIN and USER roles"));

        mockMvc.perform(patch(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(roleService).assignRoles(userId, invalidRoles);
    }
}