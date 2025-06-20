package at.msm.asobo.mappers;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.mappers.helpers.EventMapperHelper;
import at.msm.asobo.mappers.helpers.UserPictureMapperHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;


@Mapper(componentModel = "spring", uses = {UserDTOUserMapper.class, UserPictureMapperHelper.class, MediumDTOMediumMapper.class, EventMapperHelper.class})
public interface EventDTOEventMapper {
    EventDTO mapEventToEventDTO(Event event);
    Event mapEventDTOToEvent(EventDTO eventDTO);
    List<EventDTO> mapEventsToEventDTOs(List<Event> events);
    List<Event> mapEventDTOsToEvents(List<EventDTO> eventDTOs);

    @Mapping(target = "eventPicture", ignore = true)
    EventCreationDTO mapEventToEventCreationDTO(Event event);

    @Mapping(target = "pictureURI", ignore = true) // Ignore MultipartFile here
    Event mapEventCreationDTOToEvent(EventCreationDTO eventDTO);

    List<EventCreationDTO> mapEventsToEventCreationDTOs(List<Event> events);
    List<Event> mapEventCreationDTOsToEvents(List<EventDTO> eventDTOs);
}
