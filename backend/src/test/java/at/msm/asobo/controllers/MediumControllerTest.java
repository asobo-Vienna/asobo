package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.medium.MediumCreationDTO;
import at.msm.asobo.dto.medium.MediumDTO;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.services.MediumService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(MediumController.class)
@EnableMethodSecurity
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

    private UUID eventID;
    private UUID mediumID;
    private MediumDTO mediumDTO1;
    private MediumDTO mediumDTO2;
    private static final String ALL_MEDIA_URL = "/api/events/{eventID}/media";
    private static final String SINGLE_MEDIUM_URL = "/api/events/{eventID}/media/{mediumID}";

    @BeforeEach
    void setUp() {
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
    void getAllMedia_ReturnsListOfMediaByEventId() throws Exception {
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
        when(mediumService.getMediumDTOByEventIdAndMediumId(eventID, mediumID))
                .thenReturn(mediumDTO1);

        String expectedJson =  objectMapper.writeValueAsString(mediumDTO1);

        mockMvc.perform(get(SINGLE_MEDIUM_URL, eventID, mediumID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(mediumService).getMediumDTOByEventIdAndMediumId(eventID, mediumID);
    }

    @Test
    @WithMockUser(roles = "USER")
    void addMediumToEventById_WithValidData_ReturnsMedium() throws Exception {
        when(mediumService.addMediumToEventById(any(UUID.class), any(MediumCreationDTO.class)))
                .thenReturn(mediumDTO1);

        MockMultipartFile file = createMockMultipartFile();

        mockMvc.perform(multipart(ALL_MEDIA_URL, eventID)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(mediumService).addMediumToEventById(any(UUID.class), any(MediumCreationDTO.class));
    }

    @Test
    void addMediumToEventById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {

        MockMultipartFile file = createMockMultipartFile();

        mockMvc.perform(multipart(ALL_MEDIA_URL, eventID)
                        .file(file))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMediumById_WithAdminRole_DeletesMedium() throws Exception {
        when(mediumService.deleteMediumById(eventID, mediumID))
                .thenReturn(mediumDTO1);

        mockMvc.perform(delete(SINGLE_MEDIUM_URL, eventID, mediumID)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(mediumService).deleteMediumById(eventID, mediumID);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteMediumById_WithoutAdminRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete(SINGLE_MEDIUM_URL, eventID, mediumID)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(mediumService, never()).deleteMediumById(eventID, mediumID);
    }
}