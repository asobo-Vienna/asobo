package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.config.SecurityConfig;
import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.RestAuthenticationEntryPoint;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.MediumService;
import at.msm.asobo.utils.MockAuthenticationFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(MediumController.class)
@EnableMethodSecurity
@Import(SecurityConfig.class)
class MediumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MediumService mediumService;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UUID userId;
    private UUID eventID;
    private UUID mediumID;
    private MediumDTO mediumDTO1;
    private MediumDTO mediumDTO2;
    private static final String ALL_MEDIA_URL = "/api/events/{eventID}/media";
    private static final String SINGLE_MEDIUM_URL = "/api/events/{eventID}/media/{mediumID}";

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
            return new RestAuthenticationEntryPoint();
        }
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventID = UUID.randomUUID();
        mediumID = UUID.randomUUID();

        mediumDTO1 = new MediumDTO();
        mediumDTO1.setId(UUID.randomUUID());
        mediumDTO1.setEventId(eventID);

        mediumDTO2 = new MediumDTO();
        mediumDTO2.setId(UUID.randomUUID());
        mediumDTO2.setEventId(eventID);
    }

    private MockMultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "mediumFile",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test content".getBytes());
    }

    @Test
    @WithMockUser(username = "loggedInUser")
    void getAllMediaByEventId_ReturnsListOfMedia() throws Exception {
        List<MediumDTO> mediaList = List.of(mediumDTO1, mediumDTO2);
        String expectedJson =  objectMapper.writeValueAsString(mediaList);

        when(mediumService.getAllMediaByEventId(eventID)).thenReturn(mediaList);

        mockMvc.perform(get(ALL_MEDIA_URL, eventID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(mediumService).getAllMediaByEventId(eventID);
    }

    @Test
    @WithMockUser(username = "loggedInUser")
    void getMediumById_ReturnsMedium() throws Exception {
        when(mediumService.getMediumDTOByIdAndEventId(mediumID, eventID))
                .thenReturn(mediumDTO1);

        String expectedJson =  objectMapper.writeValueAsString(mediumDTO1);

        mockMvc.perform(get(SINGLE_MEDIUM_URL, eventID, mediumID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(mediumService).getMediumDTOByIdAndEventId(mediumID, eventID);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN"})
    void addMediumToEventById_WithValidData_ReturnsMedium(String role) throws Exception {
        when(mediumService.addMediumToEventById(any(UUID.class), any(MediumCreationDTO.class), any(UserPrincipal.class)))
                .thenReturn(mediumDTO1);

        MockMultipartFile file = createMockMultipartFile();

        mockMvc.perform(multipart(ALL_MEDIA_URL, eventID)
                        .file(file)
                        .with(authentication(MockAuthenticationFactory
                                .mockAuth(userId, "testuser", "testuser@test.com", role)))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(mediumService).addMediumToEventById(any(UUID.class), any(MediumCreationDTO.class), any(UserPrincipal.class));
    }

    @Test
    void addMediumToEventById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {

        MockMultipartFile file = createMockMultipartFile();

        mockMvc.perform(multipart(ALL_MEDIA_URL, eventID)
                        .file(file)
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(mediumService, never()).addMediumToEventById(eq(eventID), any(MediumCreationDTO.class), any(UserPrincipal.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN"})
    void deleteMediumById_DeletesMedium(String role) throws Exception {
        when(mediumService.deleteMediumById(eq(mediumID), eq(eventID), any(UserPrincipal.class)))
                .thenReturn(mediumDTO1);

        mockMvc.perform(delete(SINGLE_MEDIUM_URL, eventID, mediumID)
                        .with(authentication(MockAuthenticationFactory.mockAuth(
                                userId, "testuser", "testuser@test.com", role)))
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(mediumService).deleteMediumById(eq(mediumID), eq(eventID), any(UserPrincipal.class));
    }

    @Test
    void deleteMediumById_WithoutAdminRole_ReturnsForbidden() throws Exception {
        when(mediumService.deleteMediumById(eq(mediumID), eq(eventID), any(UserPrincipal.class)))
                .thenThrow(new UserNotAuthorizedException("This user is not authorized to perform this action."));

        mockMvc.perform(delete(SINGLE_MEDIUM_URL, mediumID, eventID)
                        .with(authentication(MockAuthenticationFactory.mockAuth(
                                userId, "testuser", "testuser@test.com", "USER")))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(mediumService, never()).deleteMediumById(eq(mediumID), eq(eventID), any(UserPrincipal.class));
    }
}