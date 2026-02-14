package at.msm.asobo.controllers.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.config.SecurityConfig;
import at.msm.asobo.controllers.EventController;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.dto.filter.EventFilterDTO;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.security.CustomUserDetailsService;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.RestAuthenticationEntryPoint;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.AccessControlService;
import at.msm.asobo.services.events.EventAdminService;
import at.msm.asobo.services.events.EventService;
import at.msm.asobo.utils.MockAuthenticationFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventController.class)
@EnableMethodSecurity
@Import(SecurityConfig.class)
class EventControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private EventService eventService;

  @MockitoBean private EventAdminService eventAdminService;

  @MockitoBean private FileStorageProperties fileStorageProperties;

  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private CustomUserDetailsService customUserDetailsService;

  @MockitoBean private AccessControlService accessControlService;

  private final String EVENTS_URL = "/api/events";
  private final String EVENTS_PAGINATED_URL = "/api/events/paginated";
  private final String SINGLE_EVENT_URL = "/api/events/{eventId}";
  private UUID eventId;
  private UUID userId;
  private EventSummaryDTO eventSummary1;
  private EventSummaryDTO eventSummary2;
  private EventDTO eventDTO;
  private EventUpdateDTO eventUpdateDTO;
  private EventCreationDTO eventCreationDTO;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
      return new RestAuthenticationEntryPoint();
    }
  }

  @BeforeEach
  void setUp() {
    eventId = UUID.randomUUID();
    userId = UUID.randomUUID();

    eventSummary1 = new EventSummaryDTO();
    eventSummary1.setId(eventId);

    eventSummary2 = new EventSummaryDTO();
    eventSummary2.setId(eventId);

    eventDTO = new EventDTO();
    eventDTO.setId(eventId);

    eventUpdateDTO = new EventUpdateDTO();

    eventCreationDTO = new EventCreationDTO();
    eventCreationDTO.setTitle("Test Event");
    eventCreationDTO.setDescription("Test Event");
    eventCreationDTO.setLocation("Test Location");
    eventCreationDTO.setDate(LocalDateTime.now().plusMinutes(30));
  }

  @Test
  void getAllEvents_WithoutParameters_ReturnsAllEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1, eventSummary2);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getAllEvents(any(EventFilterDTO.class))).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getAllEvents(any(EventFilterDTO.class));
  }

  @Test
  void getAllEvents_WithIsPrivateFalse_ReturnsPublicEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getAllEvents(any(EventFilterDTO.class))).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("isPrivateEvent", "false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    ArgumentCaptor<EventFilterDTO> captor = ArgumentCaptor.forClass(EventFilterDTO.class);
    verify(eventService).getAllEvents(captor.capture());
    assertEquals(false, captor.getValue().getIsPrivateEvent());
  }

  @Test
  void getAllEvents_WithIsPrivateTrue_ReturnsPrivateEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getAllEvents(any(EventFilterDTO.class))).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("isPrivateEvent", "true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    ArgumentCaptor<EventFilterDTO> captor = ArgumentCaptor.forClass(EventFilterDTO.class);
    verify(eventService).getAllEvents(captor.capture());
    assertEquals(true, captor.getValue().getIsPrivateEvent());
  }

  @Test
  void getAllEvents_WithUserIdParameter_ReturnsUserEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getEventsByParticipantId(userId, null)).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getEventsByParticipantId(userId, null);
  }

  @Test
  void getAllEventsPaginated_WithoutParameters_ReturnsAllEventsPaginated() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Page<EventSummaryDTO> eventsPage =
        new PageImpl<>(List.of(eventSummary2, eventSummary1), pageable, 2);
    String expectedJson = objectMapper.writeValueAsString(eventsPage);

    when(eventService.getAllEventsPaginated(any(EventFilterDTO.class), any(Pageable.class)))
        .thenReturn(eventsPage);

    mockMvc
        .perform(get(EVENTS_PAGINATED_URL).param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getAllEventsPaginated(any(EventFilterDTO.class), any(Pageable.class));
  }

  @Test
  void getAllEventsPaginated_WithUserId_ReturnsUserEventsPaginated() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Page<EventSummaryDTO> eventsPage = new PageImpl<>(List.of(eventSummary1), pageable, 1);
    String expectedJson = objectMapper.writeValueAsString(eventsPage);

    when(eventService.getEventsByParticipantIdPaginated(userId, null, pageable))
        .thenReturn(eventsPage);

    mockMvc
        .perform(
            get(EVENTS_PAGINATED_URL)
                .param("userId", userId.toString())
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getEventsByParticipantIdPaginated(userId, null, pageable);
  }

  @Test
  void getEventsByLocation_WithValidLocation_ReturnsEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getEventsByLocation("LA")).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("location", "LA"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getEventsByLocation("LA");
  }

  @Test
  void getEventsByLocation_WithBlankLocation_ReturnsAllEvents() throws Exception {
    List<EventSummaryDTO> events = List.of(eventSummary1, eventSummary2);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getAllEvents()).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("location", ""))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getAllEvents();
  }

  @Test
  void getEventsByDate_WithValidDate_ReturnsEvents() throws Exception {
    String validDate = "2024-12-25T14:30:00";
    List<EventSummaryDTO> events = List.of(eventSummary2, eventSummary1);
    String expectedJson = objectMapper.writeValueAsString(events);

    when(eventService.getEventsByDate(any(LocalDateTime.class))).thenReturn(events);

    mockMvc
        .perform(get(EVENTS_URL).param("date", validDate))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getEventsByDate(any(LocalDateTime.class));
  }

  @Test
  void getEventsByDate_WithInvalidDate_ReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get(EVENTS_URL).param("date", "invalid-date"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getEventById_WithValidId_ReturnsEvent() throws Exception {
    String expectedJson = objectMapper.writeValueAsString(eventDTO);

    when(eventService.getEventDTOById(eventId)).thenReturn(eventDTO);

    mockMvc
        .perform(get(SINGLE_EVENT_URL, eventId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).getEventDTOById(eventId);
  }

  @Test
  void getEventsById_WithInvalidId_ReturnsBadRequest() throws Exception {
    String invalidEventId = "invalid-id";

    mockMvc.perform(get(SINGLE_EVENT_URL, invalidEventId)).andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"USER", "ADMIN", "SUPERADMIN"})
  void createEvent_WithValidRole_ReturnsCreatedEvent(String role) throws Exception {
    String expectedJson = objectMapper.writeValueAsString(eventDTO);

    when(eventService.addNewEvent(any(EventCreationDTO.class))).thenReturn(eventDTO);

    mockMvc
        .perform(
            multipart(EVENTS_URL)
                .with(user("authenticateduser").roles(role))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventCreationDTO)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService).addNewEvent(any(EventCreationDTO.class));
  }

  @Test
  void createEvent_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc.perform(post(EVENTS_URL)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "X")
  void createEvent_WithInsufficientRole_ReturnsForbidden() throws Exception {
    mockMvc
        .perform(
            post(EVENTS_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventCreationDTO)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ROLE_ADMIN", "ROLE_SUPERADMIN", "ROLE_USER"})
  void updateEventById_CorrectRole_UpdatesEvent(String role) throws Exception {
    eventUpdateDTO.setTitle("Test Title");
    eventDTO.setTitle("Test Title");

    String inputJson = objectMapper.writeValueAsString(eventUpdateDTO);
    String expectedJson = objectMapper.writeValueAsString(eventDTO);

    when(eventService.updateEventById(
            eq(eventId), any(UserPrincipal.class), any(EventUpdateDTO.class)))
        .thenReturn(eventDTO);

    mockMvc
        .perform(
            patch(SINGLE_EVENT_URL, eventId)
                .with(
                    authentication(
                        MockAuthenticationFactory.mockAuth(
                            userId, "testuser", "testuser@test.com", role)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().string(expectedJson));

    verify(eventService)
        .updateEventById(eq(eventId), any(UserPrincipal.class), any(EventUpdateDTO.class));
  }

  @Test
  @WithMockUser(roles = "USER")
  void updateEventById_withoutCreatorUser_ReturnsForbidden() throws Exception {

    String inputJson = objectMapper.writeValueAsString(eventUpdateDTO);

    when(eventService.updateEventById(
            eq(eventId), any(UserPrincipal.class), any(EventUpdateDTO.class)))
        .thenThrow(new UserNotAuthorizedException("You are not authorized to update this event"));

    mockMvc
        .perform(
            patch(SINGLE_EVENT_URL, eventId)
                .with(
                    authentication(
                        MockAuthenticationFactory.mockAuth(
                            userId, "testuser", "testuser@test.com", "ROLE_USER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson)
                .with(csrf()))
        .andExpect(status().isForbidden());

    verify(eventService).updateEventById(eq(eventId), any(UserPrincipal.class), any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ROLE_ADMIN", "ROLE_SUPERADMIN"})
  void deleteEventById_WithAdminRole_DeletesEvent(String role) throws Exception {
    when(eventService.deleteEventById(any(UUID.class), any(UserPrincipal.class)))
        .thenReturn(eventDTO);

    mockMvc
        .perform(
            delete(SINGLE_EVENT_URL, eventId)
                .with(
                    authentication(
                        MockAuthenticationFactory.mockAuth(
                            userId, "testuser", "testuser@test.com", role))))
        .andExpect(status().isOk());

    verify(eventService).deleteEventById(any(UUID.class), any(UserPrincipal.class));
  }

  @Test
  @WithMockUser(roles = "USER")
  void deleteEventById_withCreatorUser_DeletesEvent() throws Exception {
    when(eventService.deleteEventById(any(UUID.class), any(UserPrincipal.class)))
        .thenReturn(eventDTO);

    mockMvc
        .perform(
            delete(SINGLE_EVENT_URL, eventId)
                .with(
                    authentication(
                        MockAuthenticationFactory.mockAuth(
                            userId, "testuser", "testuser@test.com", "ROLE_USER"))))
        .andExpect(status().isOk());

    verify(eventService).deleteEventById(any(UUID.class), any(UserPrincipal.class));
  }

  @Test
  @WithMockUser(roles = "USER")
  void deleteEventById_withoutCreatorUser_ReturnsForbidden() throws Exception {
    when(eventService.deleteEventById(eq(eventId), any(UserPrincipal.class)))
        .thenThrow(new UserNotAuthorizedException("You are not authorized to delete this event"));

    mockMvc
        .perform(
            delete(SINGLE_EVENT_URL, eventId)
                .with(
                    authentication(
                        MockAuthenticationFactory.mockAuth(
                            userId, "testuser", "testuser@test.com", "ROLE_USER"))))
        .andExpect(status().isForbidden());

    verify(eventService).deleteEventById(eq(eventId), any(UserPrincipal.class));
  }

  @Test
  void deleteEventById_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
    mockMvc.perform(delete(SINGLE_EVENT_URL, eventId)).andExpect(status().isUnauthorized());
  }
}
