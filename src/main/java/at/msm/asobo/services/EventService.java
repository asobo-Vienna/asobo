package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.exceptions.NotFoundException;
import at.msm.asobo.repositories.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    public List<Event> getEventsByDate(LocalDateTime date) {
        return eventRepository.findEventsByDate(date);
    }


    public Event addNewEvent(Event event) {
        return this.eventRepository.save(event);
    }


    public Event findEventByID(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    public Event deleteEventByID(UUID id) {
        Event eventToDelete = this.findEventByID(id);
        this.eventRepository.deleteById(id);
        return eventToDelete;
    }
}
