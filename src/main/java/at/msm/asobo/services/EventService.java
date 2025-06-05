package at.msm.asobo.services;

import at.msm.asobo.dto.EventCreationDTO;
import at.msm.asobo.dto.EventDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.exceptions.EventNotFoundException;
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

    public List<Event> getEventsByLocation(String location) {
        return eventRepository.findEventsByLocation(location);
    }

    public EventDTO addNewEvent(EventCreationDTO eventCreationDTO) {
        Event newEvent = new Event(eventCreationDTO);
        return new EventDTO(this.eventRepository.save(newEvent));
    }


    public Event findEventByID(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    public List<Event> findEventsByTitle(String title) {
        return eventRepository.findEventsByTitle(title);
    }

    public Event deleteEventByID(UUID id) {
        Event eventToDelete = this.findEventByID(id);
        this.eventRepository.delete(eventToDelete);
        return eventToDelete;
    }
}
