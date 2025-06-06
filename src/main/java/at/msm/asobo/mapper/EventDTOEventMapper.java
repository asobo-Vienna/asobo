package at.msm.asobo.mapper;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.mapper.helpers.EventMapperHelper;
import org.mapstruct.Mapper;
import java.util.List;


@Mapper(componentModel = "spring", uses = {UserDTOUserMapper.class, EventMapperHelper.class})
public interface EventDTOEventMapper {
    EventDTO mapEventToEventDTO(Event event);
    Event mapEventDTOToEvent(EventDTO eventDTO);
    List<EventDTO> mapEventsToEventDTOs(List<Event> events);
    List<Event> mapEventDTOsToEvents(List<EventDTO> eventDTOs);

    EventCreationDTO mapEventToEventCreationDTO(Event event);
    Event mapEventCreationDTOToEvent(EventCreationDTO eventDTO);
    List<EventCreationDTO> mapEventsToEventCreationDTOs(List<Event> events);
    List<Event> mapEventCreationDTOsToEvents(List<EventDTO> eventDTOs);
}
