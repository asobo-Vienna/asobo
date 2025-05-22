package at.msm.asobo.controllers;

import at.msm.asobo.entities.Event;
import at.msm.asobo.services.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// change this to restcontroller as soon as done testing
@Controller
@RequestMapping("/events")
public class EventController {

    private EventService eventService;


    public EventController(EventService eventService){
        this.eventService = eventService;

    }

    // TEST
    @GetMapping("/test")
    public String events(Model model) {
        return "events";
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return this.eventService.getAllEvents();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@RequestBody @Valid Event event) {
        return eventService.addNewEvent(event);
    }

    @GetMapping("/{id}")
    public Event getEventByID(@PathVariable UUID id) {
        return eventService.findEventByID(id);
    }

    @DeleteMapping("/{id}")
    public Event deleteEventByID(@PathVariable UUID id) {
        return eventService.deleteEventByID(id);
    }

}
