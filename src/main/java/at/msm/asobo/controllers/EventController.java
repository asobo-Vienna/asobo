package at.msm.asobo.controllers;

import at.msm.asobo.entities.Event;
import at.msm.asobo.services.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/events")
public class EventController {

    private EventService eventService;

    public EventController(){
        this.eventService=new EventService();
    }

    @GetMapping
    public ArrayList<Event> getAllEvents(){
        return this.eventService.getAllEvents();
    }
}
