package at.msm.asobo.controllers;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.services.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

    private EventService eventService;
    
    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDTO> getAllEvents(@RequestParam(required = false) Boolean isPrivate) {
        if (isPrivate == null) {
            return this.eventService.getAllEvents();
        } else if (isPrivate) {
            return this.eventService.getAllPrivateEvents();
        } else {
            return this.eventService.getAllPublicEvents();
        }
    }

//    @GetMapping
//    public List<EventDTO> getEventsByTitle(String title) {
//        return this.eventService.getEventsByTitle(title);
//    }


    @GetMapping(params = "location")
    public List<EventDTO> getEventsByLocation(@RequestParam(required = false) String location) {

        if (location == null || location.isBlank()) {
            return this.eventService.getAllEvents();
        }

        return this.eventService.getEventsByLocation(location);
    }


    @GetMapping(params = "date")
    public List<EventDTO> getEventsByDate(@RequestParam(required = false) String date) {
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
    public EventDTO createEvent(@ModelAttribute @Valid EventCreationDTO eventCreationDTO) {
        return this.eventService.addNewEvent(eventCreationDTO);
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable UUID id) {
        return this.eventService.getEventDTOById(id);
    }


    @DeleteMapping("/{id}")
    public EventDTO deleteEventById(@PathVariable UUID id) {
        return this.eventService.deleteEventById(id);
    }
}
