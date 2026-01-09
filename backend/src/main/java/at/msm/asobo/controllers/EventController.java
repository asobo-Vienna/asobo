package at.msm.asobo.controllers;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    
    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventSummaryDTO> getAllEvents(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Boolean isPrivate
    ) {
        if (userId != null) {
            // Fetch events where the user is a participant
            return this.eventService.getEventsByParticipantId(userId, isPrivate);
        } else if (isPrivate == null) {
            return this.eventService.getAllEvents();
        } else if (isPrivate) {
            return this.eventService.getAllPrivateEvents();
        } else {
            return this.eventService.getAllPublicEvents();
        }
    }

    @GetMapping("/paginated")
    public Page<EventSummaryDTO> getAllEventsPaginated(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Boolean isPrivate,
            Pageable pageable
    ) {
        if (userId != null) {
            return this.eventService.getEventsByParticipantIdPaginated(userId, isPrivate, pageable);
        } else if (isPrivate == null) {
            return this.eventService.getAllEventsPaginated(pageable);
        } else if (isPrivate) {
            return this.eventService.getAllPrivateEventsPaginated(pageable);
        } else {
            return this.eventService.getAllPublicEventsPaginated(pageable);
        }
    }

//    @GetMapping
//    public List<EventDTO> getEventsByTitle(String title) {
//        return this.eventService.getEventsByTitle(title);
//    }


    @GetMapping(params = "location")
    public List<EventSummaryDTO> getEventsByLocation(@RequestParam(required = false) String location, Pageable pageable) {

        if (location == null || location.isBlank()) {
            return this.eventService.getAllEvents();
        }

        return this.eventService.getEventsByLocation(location);
    }
    
    @GetMapping(params = "date")
    public List<EventSummaryDTO> getEventsByDate(@RequestParam(required = false) String date, Pageable pageable) {
        if (date == null || date.isBlank()) {
            return this.eventService.getAllEvents();
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            return this.eventService.getEventsByDate(dateTime);
        } catch (DateTimeParseException dtpe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid date format. Expected ISO-8601 format (e.g., 2024-12-01T14:30:00)", dtpe);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public EventDTO createEvent(@ModelAttribute @Valid EventCreationDTO eventCreationDTO) {
        return this.eventService.addNewEvent(eventCreationDTO);
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable UUID id) {
        return this.eventService.getEventDTOById(id);
    }

    @PatchMapping("/{id}")
    public EventDTO updateEventById(@PathVariable UUID id,
                                    @RequestBody @Valid EventUpdateDTO eventUpdateDTO,
                                    @AuthenticationPrincipal UserPrincipal loggedInUser) {
        return this.eventService.updateEventById(id, loggedInUser.getUserId(), eventUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public EventDTO deleteEventById(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal loggedInUser) {
        return this.eventService.deleteEventById(id, loggedInUser.getUserId());
    }
}
