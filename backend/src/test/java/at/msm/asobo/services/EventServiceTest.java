package at.msm.asobo.services;

import at.msm.asobo.builders.EventTestBuilder;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.services.events.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventDTOEventMapper eventDTOEventMapper;

    @InjectMocks
    private EventService eventService;

    private Event publicEvent1;
    private Event publicEvent2;
    private Event privateEvent1;
    private Event privateEvent2;
    private EventSummaryDTO publicEventSummaryDTO1;
    private EventSummaryDTO publicEventSummaryDTO2;
    private EventSummaryDTO privateEventSummaryDTO1;
    private EventSummaryDTO privateEventSummaryDTO2;
    private Pageable pageable02;
    private Pageable pageable12;
    
    @BeforeEach
    void setup() {
        // Public events
        publicEvent1 = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withTitle("Public Event 1")
                .withIsPrivateEvent(false)
                .buildEventEntity();

        publicEvent2 = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withTitle("Public Event 2")
                .withIsPrivateEvent(false)
                .buildEventEntity();

        // Private events
        privateEvent1 = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withTitle("Private Event 1")
                .withIsPrivateEvent(true)
                .buildEventEntity();

        privateEvent2 = new EventTestBuilder()
                .withId(UUID.randomUUID())
                .withTitle("Private Event 2")
                .withIsPrivateEvent(true)
                .buildEventEntity();

        publicEventSummaryDTO1 = new EventTestBuilder()
                .withId(publicEvent1.getId())
                .withIsPrivateEvent(publicEvent1.isPrivateEvent())
                .buildEventSummaryDTO();

        publicEventSummaryDTO2 = new EventTestBuilder()
                .withId(publicEvent2.getId())
                .withIsPrivateEvent(publicEvent2.isPrivateEvent())
                .buildEventSummaryDTO();

        privateEventSummaryDTO1 = new EventTestBuilder()
                .withId(privateEvent1.getId())
                .withIsPrivateEvent(privateEvent1.isPrivateEvent())
                .buildEventSummaryDTO();

        privateEventSummaryDTO2 = new EventTestBuilder()
                .withId(privateEvent2.getId())
                .withIsPrivateEvent(privateEvent2.isPrivateEvent())
                .buildEventSummaryDTO();

        pageable02 = PageRequest.of(0, 2); // page 1
        pageable12 = PageRequest.of(1, 2); // page 2
    }

    @Test
    void getAllEvents_returnsMappedEventSummaries() {
        List<Event> events = List.of(publicEvent1, publicEvent2);

        List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events))
                .thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getAllEvents();

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2);

        verify(eventRepository).findAll();
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllEvents_noEvents_returnsEmptyList() {
        when(eventRepository.findAll()).thenReturn(List.of());
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of()))
                .thenReturn(List.of());

        List<EventSummaryDTO> result = eventService.getAllEvents();

        assertNotNull(result);
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

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(publicEventSummaryDTO1, publicEventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findAllEvents(pageable02);
        verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
        verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllEventsPaginated_secondPage_returnsCorrectPage() {
        List<Event> events = List.of(privateEvent1);
        Page<Event> eventPage = new PageImpl<>(events, pageable12, 5);  // Total 5 events

        when(eventRepository.findAllEvents(pageable12)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(privateEvent1)).thenReturn(privateEventSummaryDTO1);

        Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable12);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findAllEvents(pageable12);
        verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

        when(eventRepository.findAllEvents(pageable02)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable02);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findAllEvents(pageable02);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    // TODO: adapt test to our setting
    /*@Test
    void getAllEventsPaginated_withSorting_appliesSorting() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
        List<Event> events = List.of(event2, event1);  // Sortiert
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
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events))
                .thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getAllPublicEvents();

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2);

        verify(eventRepository).findByIsPrivateEventFalse();
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPublicEvents_noPublicEvents_returnsEmptyList() {
        when(eventRepository.findByIsPrivateEventFalse()).thenReturn(List.of());
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of()))
                .thenReturn(List.of());

        List<EventSummaryDTO> result = eventService.getAllPublicEvents();

        assertNotNull(result);
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

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(publicEventSummaryDTO1, publicEventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findByIsPrivateEventFalse(pageable02);
        verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
        verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPublicEventsPaginated_secondPage_returnsCorrectPage() {
        List<Event> events = List.of(publicEvent1);
        Page<Event> eventPage = new PageImpl<>(events, pageable12, 5);  // Total 5 events

        when(eventRepository.findByIsPrivateEventFalse(pageable12)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(publicEvent1)).thenReturn(privateEventSummaryDTO1);

        Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable12);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findByIsPrivateEventFalse(pageable12);
        verify(eventDTOEventMapper).toEventSummaryDTO(publicEvent1);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPublicEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

        when(eventRepository.findByIsPrivateEventFalse(pageable02)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable02);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findByIsPrivateEventFalse(pageable02);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    // TODO: adapt test to our setting
    /*@Test
    void getAllPublicEventsPaginated_withSorting_appliesSorting() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
        List<Event> events = List.of(event2, event1);  // Sortiert
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
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events))
                .thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getAllPrivateEvents();

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2);

        verify(eventRepository).findByIsPrivateEventTrue();
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPrivateEvents_noPrivateEvents_returnsEmptyList() {
        when(eventRepository.findByIsPrivateEventTrue()).thenReturn(List.of());
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(List.of()))
                .thenReturn(List.of());

        List<EventSummaryDTO> result = eventService.getAllPrivateEvents();

        assertNotNull(result);
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

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(privateEventSummaryDTO1, privateEventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findByIsPrivateEventTrue(pageable02);
        verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
        verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPrivateEventsPaginated_secondPage_returnsCorrectPage() {
        List<Event> events = List.of(privateEvent1);
        Page<Event> eventPage = new PageImpl<>(events, pageable12, 5);  // Total 5 events

        when(eventRepository.findByIsPrivateEventTrue(pageable12)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(privateEvent1)).thenReturn(privateEventSummaryDTO1);

        Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable12);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findByIsPrivateEventTrue(pageable12);
        verify(eventDTOEventMapper).toEventSummaryDTO(privateEvent1);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPrivateEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable02, 0);

        when(eventRepository.findByIsPrivateEventTrue(pageable02)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable02);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable02);

        verify(eventRepository).findByIsPrivateEventTrue(pageable02);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    // TODO: adapt test to our setting
    /*@Test
    void getAllPrivateEventsPaginated_withSorting_appliesSorting() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("startDateTime").descending());
        List<Event> events = List.of(event2, event1);  // Sortiert
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

        when(eventRepository.findByParticipants_Id(participantId)).thenReturn(events);
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(events)).thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, null);

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2);

        verify(eventRepository).findByParticipants_Id(participantId);
        verify(eventRepository, never()).findByParticipants_IdAndIsPrivateEventTrue(any());
        verify(eventRepository, never()).findByParticipants_IdAndIsPrivateEventFalse(any());
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(events);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getEventsByParticipantId_isPrivateTrue_returnsOnlyPrivateEvents() {
        UUID participantId = UUID.randomUUID();
        List<Event> privateEvents = List.of(privateEvent1, privateEvent2);
        List<EventSummaryDTO> mappedDtos = List.of(privateEventSummaryDTO1, privateEventSummaryDTO2);

        when(eventRepository.findByParticipants_IdAndIsPrivateEventTrue(participantId))
                .thenReturn(privateEvents);
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(privateEvents))
                .thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, true);

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2)
                .allSatisfy(dto -> assertThat(dto.getIsPrivate()).isTrue());

        verify(eventRepository).findByParticipants_IdAndIsPrivateEventTrue(participantId);
        verify(eventRepository, never()).findByParticipants_Id(any());
        verify(eventRepository, never()).findByParticipants_IdAndIsPrivateEventFalse(any());
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(privateEvents);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getEventsByParticipantId_isPrivateFalse_returnsOnlyPublicEvents() {
        UUID participantId = UUID.randomUUID();
        List<Event> publicEvents = List.of(publicEvent1, publicEvent2);
        List<EventSummaryDTO> mappedDtos = List.of(publicEventSummaryDTO1, publicEventSummaryDTO2);

        when(eventRepository.findByParticipants_IdAndIsPrivateEventFalse(participantId))
                .thenReturn(publicEvents);
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(publicEvents))
                .thenReturn(mappedDtos);

        List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, false);

        assertNotNull(result);
        assertThat(result)
                .isEqualTo(mappedDtos)
                .hasSize(2)
                .allSatisfy(dto -> assertThat(dto.getIsPrivate()).isFalse());

        verify(eventRepository).findByParticipants_IdAndIsPrivateEventFalse(participantId);
        verify(eventRepository, never()).findByParticipants_Id(any());
        verify(eventRepository, never()).findByParticipants_IdAndIsPrivateEventTrue(any());
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(publicEvents);
    }

    @Test
    void getEventsByParticipantId_noEvents_returnsEmptyList() {
        UUID participantId = UUID.randomUUID();
        List<Event> emptyEvents = Collections.emptyList();
        List<EventSummaryDTO> emptyDtos = Collections.emptyList();

        when(eventRepository.findByParticipants_Id(participantId)).thenReturn(emptyEvents);
        when(eventDTOEventMapper.mapEventsToEventSummaryDTOs(emptyEvents)).thenReturn(emptyDtos);

        List<EventSummaryDTO> result = eventService.getEventsByParticipantId(participantId, null);

        assertNotNull(result);
        assertThat(result).isEmpty();

        verify(eventRepository).findByParticipants_Id(participantId);
        verify(eventDTOEventMapper).mapEventsToEventSummaryDTOs(emptyEvents);
    }
}