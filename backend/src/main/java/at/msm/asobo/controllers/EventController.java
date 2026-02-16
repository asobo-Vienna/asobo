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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
      @RequestParam(required = false) LocalDateTime date,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @RequestParam(required = false) Boolean isPrivateEvent,
      @RequestParam(required = false) Set<UUID> eventAdminIds,
      @RequestParam(required = false) Set<UUID> participantIds) {

    EventFilterDTO filterDTO =
        new EventFilterDTO(
            location,
            creatorId,
            date,
            dateFrom,
            dateTo,
            isPrivateEvent,
            eventAdminIds,
            participantIds);

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
      @RequestParam(required = false) LocalDateTime date,
      @RequestParam(required = false) LocalDateTime dateFrom,
      @RequestParam(required = false) LocalDateTime dateTo,
      @RequestParam(required = false) Boolean isPrivateEvent,
      @RequestParam(required = false) Set<UUID> eventAdminIds,
      @RequestParam(required = false) Set<UUID> participantIds,
      @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

    EventFilterDTO filterDTO =
        new EventFilterDTO(
            location,
            creatorId,
            date,
            dateFrom,
            dateTo,
            isPrivateEvent,
            eventAdminIds,
            participantIds);

    if (userId != null) {
      return this.eventService.getEventsByParticipantIdPaginated(
          userId, filterDTO.getIsPrivateEvent(), pageable);
    } else {
      return this.eventService.getAllEventsPaginated(filterDTO, pageable);
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
