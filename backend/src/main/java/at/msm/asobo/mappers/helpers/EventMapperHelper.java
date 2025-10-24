package at.msm.asobo.mappers.helpers;

import at.msm.asobo.entities.Event;
import at.msm.asobo.exceptions.EventNotFoundException;
import at.msm.asobo.repositories.EventRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventMapperHelper {

    private final EventRepository eventRepository;

    public EventMapperHelper(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Named("uuidToEvent")
    public Event fromId(UUID id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    @Named("eventToUuid")
    public UUID toId(Event event) {
        return event.getId();
    }
}
