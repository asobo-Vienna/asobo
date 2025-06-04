package at.msm.asobo.controllers;

import at.msm.asobo.dto.EventDTO;
import at.msm.asobo.entities.Event;
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
@RequestMapping("/events")
public class EventController {

    private EventService eventService;
    
    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    // TEST WITH STATIC HTML PAGES
//    @GetMapping("/test")
//    public String getEvents(Model model) {
//        return "events";
//    }
//
//    @GetMapping("/test/single-event")
//    public String getSingleEvent(Model model) {
//        return "event";
//    }


    @GetMapping
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventDTO> eventDTOS = events.stream().map(EventDTO::new).toList();
        return eventDTOS;
    }


    @GetMapping(params = "location")
    public List<EventDTO> getEventsByLocation(@RequestParam(required = false) String location) {

        if (location == null || location.isBlank()) {
            List<Event> allEvents = eventService.getAllEvents();
            return allEvents.stream().map(EventDTO::new).toList();
        }

        List<Event> events = this.eventService.getEventsByLocation(location);
        return events.stream().map(EventDTO::new).toList();
    }


    @GetMapping(params = "date")
    public List<EventDTO> getEventsByDate(@RequestParam(required = false) String date) {
        if (date == null || date.isBlank()) {
            List<Event> allEvents = eventService.getAllEvents();
            return allEvents.stream().map(EventDTO::new).toList();
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(date);
            List<Event> events = this.eventService.getEventsByDate(dateTime);
            return events.stream().map(EventDTO::new).toList();
        } catch (DateTimeParseException dtpe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid date format. Expected ISO-8601 format (e.g., 2024-12-01T14:30:00)", dtpe);
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(@RequestBody @Valid Event event) {
        Event createdEvent = this.eventService.addNewEvent(event);
        return new EventDTO(createdEvent);
    }


    @GetMapping("/{id}")
    public EventDTO getEventByID(@PathVariable UUID id) {
        Event foundEvent = this.eventService.findEventByID(id);
        return new EventDTO(foundEvent);
    }


    @DeleteMapping("/{id}")
    public EventDTO deleteEventByID(@PathVariable UUID id) {
        Event deletedEvent = this.eventService.deleteEventByID(id);
        return new EventDTO(deletedEvent);
    }
}
