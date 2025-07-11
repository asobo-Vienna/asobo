package at.msm.asobo.mappers.helpers;

import at.msm.asobo.entities.Event;
import at.msm.asobo.services.EventService;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventMapperHelper {

    private final EventService eventService;

    public EventMapperHelper(EventService eventService) {
        this.eventService = eventService;
    }

    @Named("uuidToEvent")
    public Event fromId(UUID id) {
        return eventService.getEventById(id);
    }

    @Named("eventToUuid")
    public UUID toId(Event event) {
        return event.getId();
    }
}
