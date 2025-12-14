package at.msm.asobo.controllers;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.services.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantController.class)
@EnableMethodSecurity
class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ParticipantService participantService;

    @MockitoBean
    private FileStorageProperties fileStorageProperties;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPublicDTO userPublicDTO;
    private UUID eventId;
    private final String ALL_PARTICIPANTS_URL = "/api/events/{eventID}/participants";

    @BeforeEach
    void setUp() {
        eventId =  UUID.randomUUID();
        userPublicDTO = new UserPublicDTO();
        userPublicDTO.setId(UUID.randomUUID());
    }

    @Test
    @WithMockUser(roles = "USER")
    void toggleParticipantInEvent_asUser_returns200() throws Exception {
        List<UserPublicDTO> participantDTOList = List.of(userPublicDTO);
        String expectedJson = objectMapper.writeValueAsString(participantDTOList);

        when(participantService.toggleParticipantInEvent(eq(eventId), any(UserPublicDTO.class)))
                .thenReturn(participantDTOList);

        mockMvc.perform(post(ALL_PARTICIPANTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPublicDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(participantService)
                .toggleParticipantInEvent(eq(eventId), any(UserPublicDTO.class));
    }


    @Test
    @WithMockUser(roles = "X")
    void toggleParticipantInEvent_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(post(ALL_PARTICIPANTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPublicDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(participantService);
    }

    @Test
    void toggleParticipantInEvent_unauthenticated_returns401() throws Exception {

        mockMvc.perform(post(ALL_PARTICIPANTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPublicDTO)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(participantService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void toggleParticipantInEvent_withInvalidRequestBody_returns400() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post(ALL_PARTICIPANTS_URL, eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(participantService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void toggleParticipantInEvent_withoutCsrf_returns403() throws Exception {
        mockMvc.perform(post(ALL_PARTICIPANTS_URL, eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPublicDTO)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(participantService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getParticipantsByEventId_asUser_returns200() throws Exception {
        List<UserPublicDTO> participantDTOList = List.of(userPublicDTO);
        String expectedJson = objectMapper.writeValueAsString(participantDTOList);

        when(participantService.getAllParticipantsAsDTOsByEventId(eventId))
                .thenReturn(participantDTOList);

        mockMvc.perform(get(ALL_PARTICIPANTS_URL, eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(participantService).getAllParticipantsAsDTOsByEventId(eventId);
    }

    @Test
    void getParticipantsByEventId_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(ALL_PARTICIPANTS_URL, eventId))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(participantService);
    }

    @Test
    @WithMockUser(roles = "X")
    void getParticipantsByEventId_withUnauthorizedRole_returns403() throws Exception {
        mockMvc.perform(get(ALL_PARTICIPANTS_URL, eventId))
                .andExpect(status().isForbidden());

        verifyNoInteractions(participantService);
    }

}