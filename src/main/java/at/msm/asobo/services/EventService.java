package at.msm.asobo.services;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(EventDTO::new)
                .toList();
    }

    public List<EventDTO> getEventsByDate(LocalDateTime date) {
        return eventRepository.findEventsByDate(date).stream()
                .map(EventDTO::new)
                .toList();
    }

    public List<EventDTO> getEventsByLocation(String location) {
        return eventRepository.findEventsByLocation(location).stream()
                .map(EventDTO::new)
                .toList();
    }

    public EventDTO addNewEvent(EventCreationDTO eventCreationDTO) {
        User user = userRepository.findById(eventCreationDTO.getCreator().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Event newEvent = new Event(eventCreationDTO);
        newEvent.setCreator(user);
        return new EventDTO(this.eventRepository.save(newEvent));
    }


    public Event findEventByID(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
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
