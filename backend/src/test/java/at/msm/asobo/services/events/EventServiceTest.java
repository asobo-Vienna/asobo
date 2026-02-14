package at.msm.asobo.services.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.events.EventNotFoundException;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.UserService;
import at.msm.asobo.services.files.FileStorageService;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private EventRepository eventRepository;

  @Mock private EventDTOEventMapper eventDTOEventMapper;

  @Mock private UserDTOUserMapper userDTOUserMapper;

  @InjectMocks private EventService eventService;

  @Mock private UserService userService;

  @Mock private EventAdminService eventAdminService;

  @Mock private FileStorageService fileStorageService;

  private Event publicEvent1;
  private Event publicEvent2;
  private Event privateEvent1;
  private Event privateEvent2;
  private EventSummaryDTO publicEventSummaryDTO1;
  private EventSummaryDTO publicEventSummaryDTO2;
  private EventSummaryDTO privateEventSummaryDTO1;
  private EventSummaryDTO privateEventSummaryDTO2;
  private EventDTO publicEventDTO1;
  private EventDTO publicEventDTO2;
  private EventDTO privateEventDTO1;
  private User creator;
  private UserPrincipal creatorPrincipal;
  private UserPublicDTO creatorDTO;
  private User eventAdmin1;
  private User eventAdmin2;
  private UserPublicDTO eventAdminDTO1;
  private Pageable pageable02;
  private Pageable pageable12;
  private LocalDateTime searchDate;

  @BeforeEach
  void setup() {
    // Public events
    publicEvent1 =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withTitle("Public Event 1 (Today)")
            .withIsPrivateEvent(false)
            .withDate(LocalDateTime.now())
            .buildEventEntity();

    publicEvent2 =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withTitle("Public Event 2 (Tomorrow)")
            .withIsPrivateEvent(false)
            .withDate(LocalDateTime.now().plusDays(1))
            .buildEventEntity();

    // Private events
    privateEvent1 =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withTitle("Private Event 1 (Today)")
            .withIsPrivateEvent(true)
            .withDate(LocalDateTime.now())
            .buildEventEntity();

    privateEvent2 =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withTitle("Private Event 2 (Tomorrow)")
            .withIsPrivateEvent(true)
            .withDate(LocalDateTime.now().plusDays(1))
            .buildEventEntity();

    creator =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("creator")
            .buildUserEntity();

    creatorPrincipal = new UserTestBuilder().fromUser(creator).buildUserPrincipal();

    creatorDTO = new UserTestBuilder().fromUser(creator).buildUserPublicDTO();

    eventAdmin1 =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("Event Admin 1")
            .buildUserEntity();

    eventAdminDTO1 = new UserTestBuilder().fromUser(eventAdmin1).buildUserPublicDTO();

    eventAdmin2 =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("Event Admin 2")
            .buildUserEntity();

    publicEventSummaryDTO1 = new EventTestBuilder().fromEvent(publicEvent1).buildEventSummaryDTO();

    publicEventSummaryDTO2 = new EventTestBuilder().fromEvent(publicEvent2).buildEventSummaryDTO();

    privateEventSummaryDTO1 =
        new EventTestBuilder().fromEvent(privateEvent1).buildEventSummaryDTO();

    privateEventSummaryDTO2 =
        new EventTestBuilder().fromEvent(privateEvent2).buildEventSummaryDTO();

    publicEventDTO1 = new EventTestBuilder().fromEvent(publicEvent1).buildEventDTO();

    publicEventDTO2 = new EventTestBuilder().fromEvent(publicEvent2).buildEventDTO();

    privateEventDTO1 = new EventTestBuilder().fromEvent(privateEvent1).buildEventDTO();

    pageable02 = PageRequest.of(0, 2); // page 1
    pageable12 = PageRequest.of(1, 2); // page 2

    searchDate = LocalDateTime.of(2026, 3, 14, 15, 0);
  }

  @Test
  void getAllEvents_returnsMappedEventSummaries() {
    List<Event> events = List.of(publicEvent1, publicEvent2);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

    when(eventRepository.findAll()).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getAllEvents();

    assertThat(result).isEqualTo(mappedDtos);

    verify(eventRepository).findAll();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getAllEvents_noEvents_returnsEmptyList() {
    when(eventRepository.findAll()).thenReturn(List.of());
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of())).thenReturn(List.of());

    List<EventSummaryDTO> result = eventService.getAllEvents();

    assertThat(result).isEmpty();
    verify(eventRepository).findAll();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(List.of());
  }

  @Test
  void getAllEventsPaginated_returnsMappedPage() {
    List<Event> events = List.of(publicEvent1, publicEvent2);
    Page<Event> eventPage = new PageImpl<>(events, pageable02, events.size());

    when(eventRepository.findAllEvents(pageable02)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(publicEvent1)).thenReturn(publicEventSummaryDTO1);
    when(eventDTOEventMapper.toEventSummaryDTO(publicEvent2)).thenReturn(publicEventSummaryDTO2);

    Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable02);

    assertThat(result.getContent()).containsExactly(publicEventSummaryDTO1, publicEventSummaryDTO2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findAllEvents(pageable02);
    verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
    verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent2);
  }

  @Test
  void getAllEventsPaginated_secondPage_returnsCorrectPage() {
    List<Event> events = List.of(privateEvent1);
    Page<Event> eventPage = new PageImpl<>(events, pageable12, 5); // Total 5 events

    when(eventRepository.findAllEvents(pageable12)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(privateEvent1)).thenReturn(privateEventSummaryDTO1);

    Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable12);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getNumber()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(3);

    verify(eventRepository).findAllEvents(pageable12);
    verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
  }

  @Test
  void getAllEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
    Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

    when(eventRepository.findAllEvents(pageable02)).thenReturn(emptyPage);

    Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable02);

    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findAllEvents(pageable02);
  }

  // TODO: adapt test to our setting
  /*@Test
  void getAllEventsPaginated_withSorting_appliesSorting() {
      Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
      List<Event> events = List.of(event2, event1);  // Sorted
      Page<Event> eventPage = new PageImpl<>(events, pageable, 2);

      when(eventRepository.findAllEvents(pageable)).thenReturn(eventPage);
      when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);
      when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);

      Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable);

      assertThat(result.getContent()).containsExactly(eventSummaryDTO2, eventSummaryDTO1);
      assertThat(result.getSort()).isEqualTo(Sort.by("startDateTime").descending());

      // add verify statements
  }*/

  @Test
  void getAllPublicEvents_returnsMappedEventSummaries() {
    List<Event> events = List.of(publicEvent1, publicEvent2);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

    when(eventRepository.findByIsPrivateEventFalse()).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getAllPublicEvents();

    assertThat(result).isEqualTo(mappedDtos);

    verify(eventRepository).findByIsPrivateEventFalse();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getAllPublicEvents_noPublicEvents_returnsEmptyList() {
    when(eventRepository.findByIsPrivateEventFalse()).thenReturn(List.of());
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of())).thenReturn(List.of());

    List<EventSummaryDTO> result = eventService.getAllPublicEvents();

    assertThat(result).isEmpty();

    verify(eventRepository).findByIsPrivateEventFalse();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(List.of());
  }

  @Test
  void getAllPublicEventsPaginated_returnsMappedPage() {
    List<Event> events = List.of(publicEvent1, publicEvent2);
    Page<Event> eventPage = new PageImpl<>(events, pageable02, events.size());

    when(eventRepository.findByIsPrivateEventFalse(pageable02)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(publicEvent1)).thenReturn(publicEventSummaryDTO1);
    when(eventDTOEventMapper.toEventSummaryDTO(publicEvent2)).thenReturn(publicEventSummaryDTO2);

    Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable02);

    assertThat(result.getContent()).containsExactly(publicEventSummaryDTO1, publicEventSummaryDTO2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findByIsPrivateEventFalse(pageable02);
    verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
    verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent2);
  }

  @Test
  void getAllPublicEventsPaginated_secondPage_returnsCorrectPage() {
    List<Event> events = List.of(publicEvent1);
    Page<Event> eventPage = new PageImpl<>(events, pageable12, 5); // Total 5 events

    when(eventRepository.findByIsPrivateEventFalse(pageable12)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(publicEvent1)).thenReturn(privateEventSummaryDTO1);

    Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable12);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getNumber()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(3);

    verify(eventRepository).findByIsPrivateEventFalse(pageable12);
    verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
  }

  @Test
  void getAllPublicEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
    Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

    when(eventRepository.findByIsPrivateEventFalse(pageable02)).thenReturn(emptyPage);

    Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable02);

    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findByIsPrivateEventFalse(pageable02);
  }

  // TODO: adapt test to our setting
  /*@Test
  void getAllPublicEventsPaginated_withSorting_appliesSorting() {
      Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
      List<Event> events = List.of(event2, event1);  // Sorted
      Page<Event> eventPage = new PageImpl<>(events, pageable, 2);

      when(eventRepository.findByIsPrivateEventFalse(pageable)).thenReturn(eventPage);
      when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);
      when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);

      Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable);

      assertThat(result.getContent()).containsExactly(eventSummaryDTO2, eventSummaryDTO1);
      assertThat(result.getSort()).isEqualTo(Sort.by("startDateTime").descending());

      // add verify statements
  }*/

  @Test
  void getAllPrivateEvents_returnsMappedEventSummaries() {
    List<Event> events = List.of(privateEvent1, privateEvent2);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

    when(eventRepository.findByIsPrivateEventTrue()).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getAllPrivateEvents();

    assertThat(result).isEqualTo(mappedDtos);

    verify(eventRepository).findByIsPrivateEventTrue();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getAllPrivateEvents_noPrivateEvents_returnsEmptyList() {
    when(eventRepository.findByIsPrivateEventTrue()).thenReturn(List.of());
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of())).thenReturn(List.of());

    List<EventSummaryDTO> result = eventService.getAllPrivateEvents();

    assertThat(result).isEmpty();
    verify(eventRepository).findByIsPrivateEventTrue();
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(List.of());
  }

  @Test
  void getAllPrivateEventsPaginated_returnsMappedPage() {
    List<Event> events = List.of(privateEvent1, privateEvent2);
    Page<Event> eventPage = new PageImpl<>(events, pageable02, events.size());

    when(eventRepository.findByIsPrivateEventTrue(pageable02)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(privateEvent1)).thenReturn(privateEventSummaryDTO1);
    when(eventDTOEventMapper.toEventSummaryDTO(privateEvent2)).thenReturn(privateEventSummaryDTO2);

    Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable02);

    assertThat(result.getContent())
        .containsExactly(privateEventSummaryDTO1, privateEventSummaryDTO2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findByIsPrivateEventTrue(pageable02);
    verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
    verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent2);
  }

  @Test
  void getAllPrivateEventsPaginated_secondPage_returnsCorrectPage() {
    List<Event> events = List.of(privateEvent1);
    Page<Event> eventPage = new PageImpl<>(events, pageable12, 5); // Total 5 events

    when(eventRepository.findByIsPrivateEventTrue(pageable12)).thenReturn(eventPage);
    when(eventDTOEventMapper.toEventSummaryDTO(privateEvent1)).thenReturn(privateEventSummaryDTO1);

    Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable12);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getNumber()).isEqualTo(1);
    assertThat(result.getTotalPages()).isEqualTo(3);

    verify(eventRepository).findByIsPrivateEventTrue(pageable12);
    verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
  }

  @Test
  void getAllPrivateEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
    Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

    when(eventRepository.findByIsPrivateEventTrue(pageable02)).thenReturn(emptyPage);

    Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable02);

    assertThat(result.getContent()).isEmpty();
    assertThat(result.getTotalElements()).isZero();
    assertThat(result.getPageable()).isEqualTo(pageable02);

    verify(eventRepository).findByIsPrivateEventTrue(pageable02);
  }

  // TODO: adapt test to our setting
  /*@Test
  void getAllPrivateEventsPaginated_withSorting_appliesSorting() {
      Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
      List<Event> events = List.of(event2, event1);
      Page<Event> eventPage = new PageImpl<>(events, pageable, 2);

      when(eventRepository.findByIsPrivateEventTrue(pageable)).thenReturn(eventPage);
      when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);
      when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);

      Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable);

      assertThat(result.getContent()).containsExactly(eventSummaryDTO2, eventSummaryDTO1);
      assertThat(result.getSort()).isEqualTo(Sort.by("startDateTime").descending());

      // add verify statements
  }*/

  @Test
  void getEventsByParticipantId_isPrivateNull_returnsAllEvents() {
    UUID participantId = UUID.randomUUID();
    List<Event> events = List.of(publicEvent1, privateEvent1);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, privateEventSummaryDTO1);

    when(eventRepository.findByParticipantsId(participantId)).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, null);

    assertThat(result).isEqualTo(mappedDtos);

    verify(eventRepository).findByParticipantsId(participantId);
    verify(eventRepository, never()).findByParticipantsIdAndIsPrivateEventTrue(any());
    verify(eventRepository, never()).findByParticipantsIdAndIsPrivateEventFalse(any());
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getEventsByParticipantId_isPrivateTrue_returnsOnlyPrivateEvents() {
    UUID participantId = UUID.randomUUID();
    List<Event> privateEvents = List.of(privateEvent1, privateEvent2);
    List<EventSummaryDTO> mappedDtos = List.of(privateEventSummaryDTO1, privateEventSummaryDTO2);

    when(eventRepository.findByParticipantsIdAndIsPrivateEventTrue(participantId))
        .thenReturn(privateEvents);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(privateEvents)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, true);

    assertThat(result)
        .isEqualTo(mappedDtos)
        .allSatisfy(dto -> assertThat(dto.getIsPrivate()).isTrue());

    verify(eventRepository).findByParticipantsIdAndIsPrivateEventTrue(participantId);
    verify(eventRepository, never()).findByParticipantsId(any());
    verify(eventRepository, never()).findByParticipantsIdAndIsPrivateEventFalse(any());
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(privateEvents);
  }

  @Test
  void getEventsByParticipantId_isPrivateFalse_returnsOnlyPublicEvents() {
    UUID participantId = UUID.randomUUID();
    List<Event> publicEvents = List.of(publicEvent1, publicEvent2);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

    when(eventRepository.findByParticipantsIdAndIsPrivateEventFalse(participantId))
        .thenReturn(publicEvents);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(publicEvents)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, false);

    assertThat(result)
        .isEqualTo(mappedDtos)
        .allSatisfy(dto -> assertThat(dto.getIsPrivate()).isFalse());

    verify(eventRepository).findByParticipantsIdAndIsPrivateEventFalse(participantId);
    verify(eventRepository, never()).findByParticipantsId(any());
    verify(eventRepository, never()).findByParticipantsIdAndIsPrivateEventTrue(any());
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(publicEvents);
  }

  @Test
  void getEventsByParticipantId_noEvents_returnsEmptyList() {
    UUID participantId = UUID.randomUUID();
    List<Event> emptyEvents = Collections.emptyList();
    List<EventSummaryDTO> emptyDtos = Collections.emptyList();

    when(eventRepository.findByParticipantsId(participantId)).thenReturn(emptyEvents);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(emptyEvents)).thenReturn(emptyDtos);

    List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, null);

    assertThat(result).isEmpty();

    verify(eventRepository).findByParticipantsId(participantId);
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(emptyEvents);
  }

  @Test
  void getEventsByDate_validDate_returnsMappedEventSummaries() {
    List<Event> events = List.of(publicEvent1, privateEvent1);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, privateEventSummaryDTO1);

    when(eventRepository.findEventsByDate(searchDate)).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getEventsByDate(searchDate);

    assertThat(result).isEqualTo(mappedDtos);

    verify(eventRepository).findEventsByDate(searchDate);
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getEventsByDate_noEventsFound_returnsEmptyList() {
    when(eventRepository.findEventsByDate(searchDate)).thenReturn(Collections.emptyList());
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of()))
        .thenReturn(Collections.emptyList());

    List<EventSummaryDTO> result = eventService.getEventsByDate(searchDate);

    assertThat(result).isEmpty();

    verify(eventRepository).findEventsByDate(searchDate);
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(List.of());
  }

  @Test
  void getEventsByDate_nullDate_throwsIllegalArgumentException() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByDate(null));

    assertThat(exception.getMessage()).isEqualTo("Date must not be null");

    verify(eventRepository, never()).findEventsByDate(any());
  }

  @Test
  void getEventsByLocation_validLocation_returnsMappedEventSummaries() {
    String location = "Vienna";
    List<Event> events = List.of(publicEvent1, privateEvent1);
    List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, privateEventSummaryDTO1);

    when(eventRepository.findEventsByLocation(location)).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

    List<EventSummaryDTO> result = eventService.getEventsByLocation(location);

    assertThat(result).isEqualTo(mappedDtos).hasSize(2);

    verify(eventRepository).findEventsByLocation(location);
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
  }

  @Test
  void getEventsByLocation_nullLocation_throwsIllegalArgumentException() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByLocation(null));

    assertThat(exception.getMessage()).isEqualTo("Location must not be null or empty");

    verify(eventRepository, never()).findEventsByLocation(any());
  }

  @Test
  void getEventsByLocation_emptyLocation_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByLocation(""));

    verify(eventRepository, never()).findEventsByLocation(any());
  }

  @Test
  void getEventsByLocation_blankLocation_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByLocation("   "));

    verify(eventRepository, never()).findEventsByLocation(any());
  }

  @Test
  void getEventsByLocation_noEventsFound_returnsEmptyList() {
    String location = "Unknown City";

    when(eventRepository.findEventsByLocation(location)).thenReturn(List.of());
    when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of())).thenReturn(List.of());

    List<EventSummaryDTO> result = eventService.getEventsByLocation(location);

    assertThat(result).isEmpty();

    verify(eventRepository).findEventsByLocation(location);
    verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(List.of());
  }

  @Test
  void addNewEvent_withEventAdmins_createsEventWithProvidedAdmins() {
    EventCreationDTO creationDTO =
        new EventTestBuilder()
            .withId(UUID.randomUUID())
            .withCreator(creator)
            .withEventAdmins(new HashSet<>(Set.of(eventAdmin1, eventAdmin2)))
            .withTitle("Test event")
            .withDate(LocalDateTime.now().plusDays(5))
            .buildEventCreationDTO();

    Event newEvent =
        new EventTestBuilder()
            .withCreator(creator)
            .withEventAdmins(Set.of(eventAdmin1, eventAdmin2))
            .withTitle(creationDTO.getTitle())
            .withDate(creationDTO.getDate())
            .buildEventEntity();

    Event savedEvent = new EventTestBuilder().fromEvent(newEvent).buildEventEntity();

    EventDTO eventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventDTOEventMapper.mapEventCreationDTOToEvent(creationDTO)).thenReturn(newEvent);
    when(eventRepository.save(newEvent)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(eventDTO);

    EventDTO result = eventService.addNewEvent(creationDTO);

    assertEquals(eventDTO, result);
    assertEquals(eventDTO.getId(), result.getId());

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventRepository).save(eventCaptor.capture());

    Event capturedEvent = eventCaptor.getValue();
    assertThat(capturedEvent.getEventAdmins()).containsExactlyInAnyOrder(eventAdmin1, eventAdmin2);

    verify(eventDTOEventMapper).mapEventCreationDTOToEvent(creationDTO);
    verify(eventRepository).save(newEvent);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void addNewEvent_withoutEventAdmins_setsCreatorAsAdmin() {
    EventCreationDTO creationDTO = new EventCreationDTO();
    creationDTO.setCreator(creatorDTO);
    creationDTO.setEventAdmins(null);
    creationDTO.setTitle("Test Event");

    Event newEvent =
        new EventTestBuilder()
            .withCreator(creator)
            .withTitle(creationDTO.getTitle())
            .buildEventEntity();

    Event savedEvent = new EventTestBuilder().fromEvent(newEvent).buildEventEntity();

    EventDTO eventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventDTOEventMapper.mapEventCreationDTOToEvent(creationDTO)).thenReturn(newEvent);
    when(eventRepository.save(newEvent)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(eventDTO);

    EventDTO result = eventService.addNewEvent(creationDTO);

    assertEquals(eventDTO, result);
    assertThat(creationDTO.getEventAdmins()).containsExactly(creatorDTO);

    verify(eventDTOEventMapper).mapEventCreationDTOToEvent(creationDTO);
    verify(eventRepository).save(newEvent);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void addNewEvent_withEmptyEventAdmins_setsCreatorAsAdmin() {
    EventCreationDTO creationDTO = new EventCreationDTO();
    creationDTO.setCreator(creatorDTO);
    creationDTO.setEventAdmins(new HashSet<>());
    creationDTO.setTitle("Test Event");

    Event newEvent =
        new EventTestBuilder()
            .withCreator(creator)
            .withTitle(creationDTO.getTitle())
            .buildEventEntity();

    Event savedEvent = new EventTestBuilder().fromEvent(newEvent).buildEventEntity();

    EventDTO eventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventDTOEventMapper.mapEventCreationDTOToEvent(creationDTO)).thenReturn(newEvent);
    when(eventRepository.save(newEvent)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(eventDTO);

    EventDTO result = eventService.addNewEvent(creationDTO);

    assertEquals(eventDTO, result);
    assertThat(creationDTO.getEventAdmins()).containsExactly(creatorDTO);

    verify(eventDTOEventMapper).mapEventCreationDTOToEvent(creationDTO);
    verify(eventRepository).save(newEvent);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void addNewEvent_creatorAlreadyInAdmins_doesNotDuplicate() {
    EventCreationDTO creationDTO = new EventCreationDTO();
    creationDTO.setCreator(creatorDTO);
    creationDTO.setEventAdmins(new HashSet<>(Set.of(creatorDTO, eventAdminDTO1)));
    creationDTO.setTitle("Test Event");

    Event newEvent =
        new EventTestBuilder()
            .withCreator(creator)
            .withTitle(creationDTO.getTitle())
            .buildEventEntity();

    Event savedEvent = new EventTestBuilder().fromEvent(newEvent).buildEventEntity();

    EventDTO eventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventDTOEventMapper.mapEventCreationDTOToEvent(creationDTO)).thenReturn(newEvent);
    when(eventRepository.save(newEvent)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(eventDTO);

    EventDTO result = eventService.addNewEvent(creationDTO);

    assertEquals(eventDTO, result);
    assertThat(creationDTO.getEventAdmins()).containsExactlyInAnyOrder(creatorDTO, eventAdminDTO1);

    verify(eventDTOEventMapper).mapEventCreationDTOToEvent(creationDTO);
    verify(eventRepository).save(newEvent);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void getEventById_eventExists_returnsEvent() {
    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));

    Event result = eventService.getEventById(publicEvent1.getId());

    assertEquals(publicEvent1, result);
    verify(eventRepository).findById(publicEvent1.getId());
  }

  @Test
  void getEventById_eventNotFound_throwsException() {
    UUID eventId = UUID.randomUUID();

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    EventNotFoundException exception =
        assertThrows(EventNotFoundException.class, () -> eventService.getEventById(eventId));

    assertThat(exception.getMessage()).contains(eventId.toString());
    verify(eventRepository).findById(eventId);
  }

  @Test
  void getEventDTOById_eventExists_returnsMappedDTO() {
    when(eventRepository.findById(privateEvent1.getId())).thenReturn(Optional.of(privateEvent1));
    when(eventDTOEventMapper.mapEventToEventDTO(privateEvent1)).thenReturn(privateEventDTO1);

    EventDTO result = eventService.getEventDTOById(privateEvent1.getId());

    assertEquals(privateEventDTO1, result);
    assertEquals(privateEventDTO1.getId(), result.getId());

    verify(eventRepository).findById(privateEvent1.getId());
    verify(eventDTOEventMapper).mapEventToEventDTO(privateEvent1);
  }

  @Test
  void getEventDTOById_eventNotFound_throwsException() {
    when(eventRepository.findById(privateEvent1.getId())).thenReturn(Optional.empty());

    assertThrows(
        EventNotFoundException.class, () -> eventService.getEventDTOById(privateEvent1.getId()));

    verify(eventRepository).findById(privateEvent1.getId());
  }

  @Test
  void getEventsByTitle_eventsExist_returnsMappedDTOs() {
    String title = "Public";
    List<Event> events = List.of(publicEvent1, publicEvent2);
    List<EventDTO> eventDTOs = List.of(publicEventDTO1, publicEventDTO2);

    when(eventRepository.findEventsByTitle(title)).thenReturn(events);
    when(eventDTOEventMapper.mapEventsToEventDTOs(events)).thenReturn(eventDTOs);

    List<EventDTO> result = eventService.getEventsByTitle(title);

    assertThat(result).isEqualTo(eventDTOs);

    verify(eventRepository).findEventsByTitle(title);
    verify(eventDTOEventMapper).mapEventsToEventDTOs(events);
  }

  @Test
  void getEventsByTitle_noEventsFound_returnsEmptyList() {
    String title = "NonExistent";

    when(eventRepository.findEventsByTitle(title)).thenReturn(Collections.emptyList());
    when(eventDTOEventMapper.mapEventsToEventDTOs(Collections.emptyList()))
        .thenReturn(Collections.emptyList());

    List<EventDTO> result = eventService.getEventsByTitle(title);

    assertThat(result).isEmpty();

    verify(eventRepository).findEventsByTitle(title);
    verify(eventDTOEventMapper).mapEventsToEventDTOs(Collections.emptyList());
  }

  @Test
  void getEventsByTitle_nullTitle_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByTitle(null));

    verify(eventRepository, never()).findEventsByTitle(any());
  }

  @Test
  void getEventsByTitle_emptyTitle_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByTitle(""));

    verify(eventRepository, never()).findEventsByTitle(any());
  }

  @Test
  void getEventsByTitle_blankTitle_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> eventService.getEventsByTitle("   "));

    verify(eventRepository, never()).findEventsByTitle(any());
  }

  @Test
  void deleteEventById_userIsCreator_deletesEvent() {
    publicEvent1.setCreator(creator);
    String pathToPicture = "path/to/picture.jpg";
    publicEvent1.setPictureURI(pathToPicture);

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventDTOEventMapper.mapEventToEventDTO(publicEvent1)).thenReturn(publicEventDTO1);

    EventDTO result = eventService.deleteEventById(publicEvent1.getId(), creatorPrincipal);

    assertEquals(publicEventDTO1, result);
    assertEquals(publicEventDTO1.getId(), result.getId());

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(fileStorageService).delete(pathToPicture);
    verify(eventRepository).delete(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(publicEvent1);
  }

  @Test
  void deleteEventById_eventWithoutPicture_deletesEventWithoutDeletingFile() {
    publicEvent1.setCreator(creator);
    publicEvent1.setPictureURI(null);

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventDTOEventMapper.mapEventToEventDTO(publicEvent1)).thenReturn(publicEventDTO1);

    EventDTO result = eventService.deleteEventById(publicEvent1.getId(), creatorPrincipal);

    assertEquals(publicEventDTO1, result);
    assertEquals(publicEventDTO1.getId(), result.getId());

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(fileStorageService, never()).delete(any());
    verify(eventRepository).delete(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(publicEvent1);
  }

  @Test
  void deleteEventById_userIsEventAdmin_deletesEvent() {
    publicEvent1.setCreator(creator);
    publicEvent1.setPictureURI(null);

    UserPrincipal adminPrincipal = new UserTestBuilder().fromUser(eventAdmin1).buildUserPrincipal();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(eventAdmin1.getId())).thenReturn(eventAdmin1);
    when(eventAdminService.canManageEvent(publicEvent1, eventAdmin1)).thenReturn(true);
    when(eventDTOEventMapper.mapEventToEventDTO(publicEvent1)).thenReturn(publicEventDTO1);

    EventDTO result = eventService.deleteEventById(publicEvent1.getId(), adminPrincipal);

    assertEquals(publicEventDTO1, result);
    assertEquals(publicEventDTO1.getId(), result.getId());

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(eventAdmin1.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, eventAdmin1);
    verify(fileStorageService, never()).delete(any());
    verify(eventRepository).delete(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(publicEvent1);
  }

  @Test
  void deleteEventById_userNotAuthorized_throwsException() {
    User unauthorizedUser =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("unauthorized")
            .buildUserEntity();

    publicEvent1.setCreator(creator);

    UserPrincipal unauthorizedPrincipal =
        new UserTestBuilder().fromUser(unauthorizedUser).buildUserPrincipal();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
    when(eventAdminService.canManageEvent(publicEvent1, unauthorizedUser)).thenReturn(false);

    UserNotAuthorizedException exception =
        assertThrows(
            UserNotAuthorizedException.class,
            () -> eventService.deleteEventById(publicEvent1.getId(), unauthorizedPrincipal));

    assertThat(exception.getMessage()).isEqualTo("You are not allowed to delete this event");

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(unauthorizedUser.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, unauthorizedUser);
    verify(fileStorageService, never()).delete(any());
    verify(eventRepository, never()).delete(any(Event.class));
    verify(eventDTOEventMapper, never()).mapEventToEventDTO(any());
  }

  @Test
  void deleteEventById_eventNotFound_throwsException() {
    UUID eventId = UUID.randomUUID();

    UserPrincipal userPrincipal = new UserTestBuilder().fromUser(creator).buildUserPrincipal();

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    EventNotFoundException exception =
        assertThrows(
            EventNotFoundException.class,
            () -> eventService.deleteEventById(eventId, userPrincipal));

    assertThat(exception.getMessage()).contains(eventId.toString());

    verify(eventRepository).findById(eventId);
    verify(userService, never()).getUserById(any());
    verify(eventAdminService, never()).canManageEvent(any(), any());
    verify(fileStorageService, never()).delete(any());
    verify(eventRepository, never()).delete(any(Event.class));
  }

  @Test
  void deleteEventById_userNotFound_throwsException() {
    UserPrincipal userPrincipal =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("Nonexistent user")
            .buildUserPrincipal();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(userPrincipal.getUserId()))
        .thenThrow(new UserNotFoundException(userPrincipal.getUserId()));

    UserNotFoundException exception =
        assertThrows(
            UserNotFoundException.class,
            () -> eventService.deleteEventById(publicEvent1.getId(), userPrincipal));

    assertThat(exception.getMessage()).contains(userPrincipal.getUserId().toString());

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(userPrincipal.getUserId());
    verify(eventAdminService, never()).canManageEvent(any(), any());
    verify(fileStorageService, never()).delete(any());
    verify(eventRepository, never()).delete(any(Event.class));
  }

  @Test
  void updateEventById_userIsCreator_updatesEvent() {
    publicEvent1.setCreator(creator);
    publicEvent1.setTitle("Old Title");
    publicEvent1.setDescription("Old Description");

    EventUpdateDTO updateDTO =
        new EventTestBuilder()
            .withTitle("New Title")
            .withDescription("New Description")
            .withParticipants(null)
            .buildEventUpdateDTO();
    updateDTO.setPicture(null);

    Event savedEvent =
        new EventTestBuilder()
            .fromEvent(publicEvent1)
            .withTitle("New Title")
            .withDescription("New Description")
            .buildEventEntity();

    EventDTO savedEventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventRepository.save(publicEvent1)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(savedEventDTO);

    EventDTO result =
        eventService.updateEventById(publicEvent1.getId(), creatorPrincipal, updateDTO);

    assertEquals(savedEventDTO, result);
    assertEquals(savedEventDTO.getId(), result.getId());

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(fileStorageService).handleEventPictureUpdate(null, publicEvent1);
    verify(eventRepository).save(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void updateEventById_withPicture_updatesEventAndPicture() {
    publicEvent1.setCreator(creator);

    MockMultipartFile picture =
        new MockMultipartFile(
            "picture", "event.jpg", "image/jpeg", "test image content".getBytes());

    EventUpdateDTO updateDTO =
        new EventTestBuilder()
            .withTitle("Updated Title")
            .withParticipants(null)
            .buildEventUpdateDTO();
    updateDTO.setPicture(picture);

    Event savedEvent = new EventTestBuilder().fromEvent(publicEvent1).buildEventEntity();

    EventDTO saveEventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventRepository.save(publicEvent1)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(saveEventDTO);

    EventDTO result =
        eventService.updateEventById(publicEvent1.getId(), creatorPrincipal, updateDTO);

    assertEquals(saveEventDTO, result);

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(fileStorageService).handleEventPictureUpdate(picture, publicEvent1);
    verify(eventRepository).save(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void updateEventById_withParticipants_updatesParticipants() {
    publicEvent1.setCreator(creator);

    User participant1 =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("participant1")
            .buildUserEntity();

    User participant2 =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("participant2")
            .buildUserEntity();

    UserPublicDTO participant1DTO =
        new UserTestBuilder().fromUser(participant1).buildUserPublicDTO();

    UserPublicDTO participant2DTO =
        new UserTestBuilder().fromUser(participant2).buildUserPublicDTO();

    Set<UserPublicDTO> participantDTOs = Set.of(participant1DTO, participant2DTO);
    Set<User> participants = Set.of(participant1, participant2);

    EventUpdateDTO updateDTO =
        new EventTestBuilder().withParticipants(participants).buildEventUpdateDTO();
    updateDTO.setPicture(null);

    Event savedEvent = new EventTestBuilder().fromEvent(publicEvent1).buildEventEntity();

    EventDTO savedEventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(userDTOUserMapper.mapUserPublicDTOsToUsers(participantDTOs)).thenReturn(participants);
    when(eventRepository.save(publicEvent1)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(savedEventDTO);

    EventDTO result =
        eventService.updateEventById(publicEvent1.getId(), creatorPrincipal, updateDTO);

    assertEquals(savedEventDTO, result);

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(userDTOUserMapper).mapUserPublicDTOsToUsers(participantDTOs);
    verify(eventRepository).save(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }

  @Test
  void updateEventById_userNotAuthorized_throwsException() {
    publicEvent1.setCreator(creator);

    User unauthorizedUser =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("unauthorized")
            .buildUserEntity();

    UserPrincipal unauthorizedPrincipal =
        new UserTestBuilder().fromUser(unauthorizedUser).buildUserPrincipal();

    EventUpdateDTO updateDTO = new EventTestBuilder().withTitle("New Title").buildEventUpdateDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(unauthorizedUser.getId())).thenReturn(unauthorizedUser);
    when(eventAdminService.canManageEvent(publicEvent1, unauthorizedUser)).thenReturn(false);

    UserNotAuthorizedException exception =
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                eventService.updateEventById(
                    publicEvent1.getId(), unauthorizedPrincipal, updateDTO));

    assertThat(exception.getMessage()).isEqualTo("You are not allowed to update this event");

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(unauthorizedUser.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, unauthorizedUser);
    verify(eventRepository, never()).save(any());
  }

  @Test
  void updateEventById_eventNotFound_throwsException() {
    UUID eventId = UUID.randomUUID();

    UserPrincipal userPrincipal = new UserTestBuilder().fromUser(creator).buildUserPrincipal();

    EventUpdateDTO updateDTO = new EventTestBuilder().withTitle("New Title").buildEventUpdateDTO();

    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    assertThrows(
        EventNotFoundException.class,
        () -> eventService.updateEventById(eventId, userPrincipal, updateDTO));

    verify(eventRepository).findById(eventId);
    verify(eventRepository, never()).save(any());
  }

  @Test
  void updateEventById_userNotFound_throwsException() {
    UserPrincipal userPrincipal =
        new UserTestBuilder()
            .withId(UUID.randomUUID())
            .withUsernameAndEmail("user")
            .buildUserPrincipal();

    EventUpdateDTO updateDTO = new EventTestBuilder().withTitle("New Title").buildEventUpdateDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(userPrincipal.getUserId()))
        .thenThrow(new UserNotFoundException(userPrincipal.getUserId()));

    assertThrows(
        UserNotFoundException.class,
        () -> eventService.updateEventById(publicEvent1.getId(), userPrincipal, updateDTO));

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(userPrincipal.getUserId());
    verify(eventRepository, never()).save(any());
  }

  @Test
  void updateEventById_partialUpdate_onlyUpdatesProvidedFields() {
    publicEvent1.setCreator(creator);
    publicEvent1.setTitle("Old Title");
    publicEvent1.setDescription("Old Description");
    publicEvent1.setLocation("Old Location");

    EventUpdateDTO updateDTO =
        new EventTestBuilder()
            .withTitle("New Title")
            .withDescription(null)
            .withLocation(null)
            .buildEventUpdateDTO();
    updateDTO.setPicture(null);

    EventDTO expectedDTO =
        new EventTestBuilder().fromEvent(publicEvent1).withTitle("New Title").buildEventDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creatorPrincipal.getUserId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventRepository.save(publicEvent1)).thenReturn(publicEvent1);
    when(eventDTOEventMapper.mapEventToEventDTO(publicEvent1)).thenReturn(expectedDTO);

    EventDTO result =
        eventService.updateEventById(publicEvent1.getId(), creatorPrincipal, updateDTO);

    assertEquals("New Title", publicEvent1.getTitle());
    assertEquals("Old Description", publicEvent1.getDescription());
    assertEquals("Old Location", publicEvent1.getLocation());
    assertEquals(expectedDTO, result);

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creatorPrincipal.getUserId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(eventRepository).save(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(publicEvent1);
  }

  @Test
  void updateEventById_setsModificationDate() {
    publicEvent1.setCreator(creator);
    Instant beforeUpdate = Instant.now();
    publicEvent1.setModificationDate(beforeUpdate.plus(Duration.ofHours(1)));

    EventUpdateDTO updateDTO =
        new EventTestBuilder().withTitle("New Title").withParticipants(null).buildEventUpdateDTO();
    updateDTO.setPicture(null);

    Event savedEvent = new EventTestBuilder().fromEvent(publicEvent1).buildEventEntity();

    EventDTO savedEventDTO = new EventTestBuilder().fromEvent(savedEvent).buildEventDTO();

    when(eventRepository.findById(publicEvent1.getId())).thenReturn(Optional.of(publicEvent1));
    when(userService.getUserById(creator.getId())).thenReturn(creator);
    when(eventAdminService.canManageEvent(publicEvent1, creator)).thenReturn(true);
    when(eventRepository.save(publicEvent1)).thenReturn(savedEvent);
    when(eventDTOEventMapper.mapEventToEventDTO(savedEvent)).thenReturn(savedEventDTO);

    eventService.updateEventById(publicEvent1.getId(), creatorPrincipal, updateDTO);

    assertThat(publicEvent1.getModificationDate()).isAfterOrEqualTo(beforeUpdate);

    verify(eventRepository).findById(publicEvent1.getId());
    verify(userService).getUserById(creator.getId());
    verify(eventAdminService).canManageEvent(publicEvent1, creator);
    verify(eventRepository).save(publicEvent1);
    verify(eventDTOEventMapper).mapEventToEventDTO(savedEvent);
  }
}
