package at.msm.asobo.mapper.helpers;

import at.msm.asobo.entities.Event;
import at.msm.asobo.services.EventService;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventMapperHelper {

    @Autowired // consider removing this
    private EventService eventService;

    @Named("uuidToEvent")
    public Event fromId(UUID id) {
        return eventService.getEventById(id);
    }

    @Named("eventToUuid")
    public UUID toId(Event event) {
        return event.getId();
    }
}
