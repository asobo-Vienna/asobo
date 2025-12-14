package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.repositories.RoleRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private RoleRepository roleRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UUID userId;
    private Role testRole;
    private User testUser;
    private final String ROLES_URL = "/api/roles";
    private final String ASSIGN_URL = "/api/roles/assign";

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("TESTROLE");

        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setRoles(new HashSet<>());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void getAllRoles_withAuthorizedRole_returns200(String role) throws Exception {
        List<Role> rolesList = List.of(testRole);
        when(roleRepository.findAll()).thenReturn(rolesList);

        String expectedJson = objectMapper.writeValueAsString(List.of("TESTROLE"));

        mockMvc.perform(get(ROLES_URL)
                        .with(user("testuser").roles(role)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(roleRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get(ROLES_URL))
                .andExpect(status().isForbidden());

        verifyNoInteractions(roleRepository);
    }

    @Test
    void getAllRoles_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(ROLES_URL))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(roleRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withAuthorizedRole_returns200(String role) throws Exception {
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("TESTROLE")).thenReturn(Optional.of(testRole));
        when(userRepository.save(testUser)).thenReturn(testUser);

        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("userId", userId.toString())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Role assigned"));

        verify(userRepository).findUserById(userId);
        verify(roleRepository).findByName("TESTROLE");
        verify(userRepository).save(testUser);
    }

    @Test
    @WithMockUser(roles = "USER")
    void assignRole_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .with(csrf())
                        .param("userId", userId.toString())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userRepository, roleRepository);
    }

    @Test
    void assignRole_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .with(csrf())
                        .param("userId", userId.toString())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userRepository, roleRepository);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignRole_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .param("userId", userId.toString())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userRepository, roleRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withUserNotFound_returns404(String role) throws Exception {
        when(userRepository.findUserById(userId))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("userId", userId.toString())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isNotFound());

        verify(userRepository).findUserById(userId);
        verifyNoInteractions(roleRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withRoleNotFound_returns404(String role) throws Exception {
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("NONEXISTENT"))
                .thenThrow(new RoleNotFoundException("Role not found"));

        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("userId", userId.toString())
                        .param("roleName", "NONEXISTENT"))
                .andExpect(status().isNotFound());

        verify(userRepository).findUserById(userId);
        verify(roleRepository).findByName("NONEXISTENT");
        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withMissingUserId_returns400(String role) throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userRepository, roleRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withMissingRoleName_returns400(String role) throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("userId", userId.toString()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userRepository, roleRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "SUPERADMIN"})
    void assignRole_withInvalidUUID_returns400(String role) throws Exception {
        mockMvc.perform(post(ASSIGN_URL)
                        .with(user("testuser").roles(role))
                        .with(csrf())
                        .param("userId", "invalid-uuid")
                        .param("roleName", "TESTROLE"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userRepository, roleRepository);
    }
}