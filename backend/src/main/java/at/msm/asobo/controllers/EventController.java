package at.msm.asobo.controllers;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.dto.filter.EventFilterDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventAdminService;
import at.msm.asobo.services.events.EventService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/events")
public class EventController {
  private final EventService eventService;
  private final EventAdminService eventAdminService;

  public EventController(EventService eventService, EventAdminService eventAdminService) {
    this.eventService = eventService;
    this.eventAdminService = eventAdminService;
  }

  @GetMapping
  public List<EventSummaryDTO> getAllEvents(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) UUID creatorId,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @RequestParam(required = false) Boolean isPrivateEvent,
      @RequestParam(required = false) Set<UUID> eventAdminIds,
      @RequestParam(required = false) Set<UUID> participantIds) {

    EventFilterDTO filterDTO =
        new EventFilterDTO(
            location, creatorId, dateFrom, dateTo, isPrivateEvent, eventAdminIds, participantIds);

    if (userId != null) {
      return this.eventService.getEventsByParticipantId(userId, isPrivateEvent);
    } else {
      return this.eventService.getAllEvents(filterDTO);
    }
  }

  @GetMapping("/paginated")
  public Page<EventSummaryDTO> getAllEventsPaginated(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) UUID creatorId,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @RequestParam(required = false) Boolean isPrivateEvent,
      @RequestParam(required = false) Set<UUID> eventAdminIds,
      @RequestParam(required = false) Set<UUID> participantIds,
      Pageable pageable) {

    EventFilterDTO filterDTO =
        new EventFilterDTO(
            location, creatorId, dateFrom, dateTo, isPrivateEvent, eventAdminIds, participantIds);

    if (userId != null) {
      return this.eventService.getEventsByParticipantIdPaginated(
          userId, filterDTO.getIsPrivateEvent(), pageable);
    } else {
      return this.eventService.getAllEventsPaginated(filterDTO, pageable);
    }
  }

  @GetMapping(params = "location")
  public List<EventSummaryDTO> getEventsByLocation(
      @RequestParam(required = false) String location, Pageable pageable) {

    if (location == null || location.isBlank()) {
      return this.eventService.getAllEvents();
    }

    return this.eventService.getEventsByLocation(location);
  }

  @GetMapping(params = "date")
  public List<EventSummaryDTO> getEventsByDate(
      @RequestParam(required = false) String date, Pageable pageable) {
    if (date == null || date.isBlank()) {
      return this.eventService.getAllEvents();
    }

    try {
      LocalDateTime dateTime = LocalDateTime.parse(date);
      return this.eventService.getEventsByDate(dateTime);
    } catch (DateTimeParseException dtpe) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Invalid date format. Expected ISO-8601 format (e.g., 2024-12-01T14:30:00)",
          dtpe);
    }
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
  public EventDTO createEvent(@RequestBody @Valid EventCreationDTO eventCreationDTO) {
    return this.eventService.addNewEvent(eventCreationDTO);
  }

  @PatchMapping("/{id}/picture")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
  public EventDTO updateEventPicture(
      @PathVariable UUID id,
      @RequestParam("eventPicture") MultipartFile eventPicture,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {

    EventUpdateDTO eventUpdateDTO = new EventUpdateDTO();
    eventUpdateDTO.setPicture(eventPicture);

    return this.eventService.updateEventById(id, loggedInUser, eventUpdateDTO);
  }

  @GetMapping("/{id}")
  public EventDTO getEventById(@PathVariable UUID id) {
    return this.eventService.getEventDTOById(id);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
  public EventDTO updateEventById(
      @PathVariable UUID id,
      @RequestBody @Valid EventUpdateDTO eventUpdateDTO,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.eventService.updateEventById(id, loggedInUser, eventUpdateDTO);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
  public EventDTO deleteEventById(
      @PathVariable UUID id, @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return this.eventService.deleteEventById(id, loggedInUser);
  }

  @PatchMapping("/{eventId}/addAdmins")
  @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public EventDTO addEventAdmin(
      @PathVariable UUID eventId,
      @RequestBody Set<UUID> userIds,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return eventAdminService.addAdminsToEvent(eventId, userIds, loggedInUser);
  }

  @DeleteMapping("/{eventId}/removeAdmins")
  @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
  public EventDTO removeEventAdmin(
      @PathVariable UUID eventId,
      @RequestBody Set<UUID> userIds,
      @AuthenticationPrincipal UserPrincipal loggedInUser) {
    return eventAdminService.removeAdminsFromEvent(eventId, userIds, loggedInUser);
  }
}
