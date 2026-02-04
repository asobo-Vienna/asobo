package at.msm.asobo.services;

import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.services.events.EventService;
import org.glassfish.jaxb.core.v2.TODO;
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

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    private EventService eventService;

    private UUID event1Id;
    private UUID event2Id;
    private UUID event3Id;
    private Event event1;
    private Event event2;
    private Event event3;
    private EventSummaryDTO eventSummaryDTO1;
    private EventSummaryDTO eventSummaryDTO2;
    private EventSummaryDTO eventSummaryDTO3;


    @BeforeEach
    void setup() {
        event1Id = UUID.randomUUID();
        event2Id = UUID.randomUUID();
        event3Id = UUID.randomUUID();

        event1 = new Event();
        event1.setId(event1Id);
        event1.setTitle("Event 1");

        event2 = new Event();
        event2.setId(event2Id);
        event2.setTitle("Event 2");

        event3 = new Event();
        event3.setId(event3Id);
        event3.setTitle("Event 3");

        eventSummaryDTO1 = new EventSummaryDTO();
        eventSummaryDTO1.setId(event1Id);

        eventSummaryDTO2 = new EventSummaryDTO();
        eventSummaryDTO2.setId(event2Id);

        eventSummaryDTO3 = new EventSummaryDTO();
        eventSummaryDTO3.setId(event3Id);
    }

    @Test
    void getAllEvents_returnsMappedEventSummaries() {
        List<Event> events = List.of(event1, event2);

        List<EventSummaryDTO> mappedDtos = List.of(eventSummaryDTO1, eventSummaryDTO2);

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
        Pageable pageable = PageRequest.of(0, 2);
        List<Event> events = List.of(event1, event2);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        when(eventRepository.findAllEvents(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);
        when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);

        Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(eventSummaryDTO1, eventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findAllEvents(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event1);
        verify(eventDTOEventMapper).toEventSummaryDTO(event2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllEventsPaginated_secondPage_returnsCorrectPage() {
        Pageable pageable = PageRequest.of(1, 2); // page 2
        List<Event> events = List.of(event3);
        Page<Event> eventPage = new PageImpl<>(events, pageable, 5);  // Total 5 events

        when(eventRepository.findAllEvents(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event3)).thenReturn(eventSummaryDTO3);

        Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findAllEvents(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event3);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(eventRepository.findAllEvents(pageable)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findAllEvents(pageable);
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
        List<Event> events = List.of(event1, event2);

        List<EventSummaryDTO> mappedDtos = List.of(eventSummaryDTO1, eventSummaryDTO2);

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
        Pageable pageable = PageRequest.of(0, 2);
        List<Event> events = List.of(event1, event2);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        when(eventRepository.findByIsPrivateEventFalse(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);
        when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);

        Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(eventSummaryDTO1, eventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findByIsPrivateEventFalse(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event1);
        verify(eventDTOEventMapper).toEventSummaryDTO(event2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPublicEventsPaginated_secondPage_returnsCorrectPage() {
        Pageable pageable = PageRequest.of(1, 2); // page 2
        List<Event> events = List.of(event3);
        Page<Event> eventPage = new PageImpl<>(events, pageable, 5);  // Total 5 events

        when(eventRepository.findByIsPrivateEventFalse(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event3)).thenReturn(eventSummaryDTO3);

        Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findByIsPrivateEventFalse(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event3);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPublicEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(eventRepository.findByIsPrivateEventFalse(pageable)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllPublicEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findByIsPrivateEventFalse(pageable);
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
        List<Event> events = List.of(event1, event2);

        List<EventSummaryDTO> mappedDtos = List.of(eventSummaryDTO1, eventSummaryDTO2);

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
        Pageable pageable = PageRequest.of(0, 2);
        List<Event> events = List.of(event1, event2);
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());

        when(eventRepository.findByIsPrivateEventTrue(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event1)).thenReturn(eventSummaryDTO1);
        when(eventDTOEventMapper.toEventSummaryDTO(event2)).thenReturn(eventSummaryDTO2);

        Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).containsExactly(eventSummaryDTO1, eventSummaryDTO2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findByIsPrivateEventTrue(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event1);
        verify(eventDTOEventMapper).toEventSummaryDTO(event2);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPrivateEventsPaginated_secondPage_returnsCorrectPage() {
        Pageable pageable = PageRequest.of(1, 2); // page 2
        List<Event> events = List.of(event3);
        Page<Event> eventPage = new PageImpl<>(events, pageable, 5);  // Total 5 events

        when(eventRepository.findByIsPrivateEventTrue(pageable)).thenReturn(eventPage);
        when(eventDTOEventMapper.toEventSummaryDTO(event3)).thenReturn(eventSummaryDTO3);

        Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(eventRepository).findByIsPrivateEventTrue(pageable);
        verify(eventDTOEventMapper).toEventSummaryDTO(event3);
        verifyNoMoreInteractions(eventRepository, eventDTOEventMapper);
    }

    @Test
    void getAllPrivateEventsPaginated_whenEmpty_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Event> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(eventRepository.findByIsPrivateEventTrue(pageable)).thenReturn(emptyPage);

        Page<EventSummaryDTO> result = eventService.getAllPrivateEventsPaginated(pageable);

        assertNotNull(result);
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getPageable()).isEqualTo(pageable);

        verify(eventRepository).findByIsPrivateEventTrue(pageable);
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
}