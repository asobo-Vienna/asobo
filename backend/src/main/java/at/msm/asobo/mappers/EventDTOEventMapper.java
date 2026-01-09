package at.msm.asobo.mappers;

import at.msm.asobo.dto.event.EventCreationDTO;
import at.msm.asobo.dto.event.EventDTO;
import at.msm.asobo.dto.event.EventSummaryDTO;
import at.msm.asobo.dto.event.EventUpdateDTO;
import at.msm.asobo.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventDTOEventMapper {

    private final UserDTOUserMapper userDTOUserMapper;
    private final UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper;
    private final MediumDTOMediumMapper mediumDTOMediumMapper;

    public EventDTOEventMapper(
            UserDTOUserMapper userDTOUserMapper,
            UserCommentDTOUserCommentMapper userCommentDTOUserCommentMapper,
            MediumDTOMediumMapper mediumDTOMediumMapper) {
        this.userDTOUserMapper = userDTOUserMapper;
        this.userCommentDTOUserCommentMapper = userCommentDTOUserCommentMapper;
        this.mediumDTOMediumMapper = mediumDTOMediumMapper;
    }

    // Event → EventDTO
    public EventDTO mapEventToEventDTO(Event event) {
        if (event == null) {
            return null;
        }

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setPictureURI(event.getPictureURI());
        dto.setLocation(event.getLocation());
        dto.setDate(event.getDate());
        dto.setCreationDate(event.getCreationDate());
        dto.setModificationDate(event.getModificationDate());
        dto.setCreator(this.userDTOUserMapper.mapUserToUserPublicDTO(event.getCreator()));
        dto.setIsPrivate(event.isPrivateEvent());

        // Map participants
        if (event.getParticipants() != null) {
            dto.setParticipants(userDTOUserMapper.mapUsersToUserPublicDTOs(event.getParticipants()));
        }

        // Map comments
        if (event.getComments() != null) {
            dto.setComments(userCommentDTOUserCommentMapper.mapUserCommentsToUserCommentDTOs(event.getComments()));
        }

        // Map media
        if (event.getMedia() != null) {
            dto.setMedia(mediumDTOMediumMapper.mapMediaToMediaDTOList(event.getMedia()));
        }

        return dto;
    }

    // EventDTO → Event
    public Event mapEventDTOToEvent(EventDTO dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setPictureURI(dto.getPictureURI());
        event.setLocation(dto.getLocation());
        event.setDate(dto.getDate());
        event.setCreationDate(dto.getCreationDate());
        event.setModificationDate(dto.getModificationDate());
        event.setCreator(this.userDTOUserMapper.mapUserPublicDTOToUser(dto.getCreator()));
        event.setPrivateEvent(dto.getIsPrivate());

        // TODO: this is where the replacing of mappers to factory patterns ticket #37 comes in
        // Note: participants, comments, and media should be handled separately
        // in the service layer with proper entity references

        return event;
    }

    public EventSummaryDTO toEventSummaryDTO(Event event) {
        if (event == null) {
            return null;
        }

        EventSummaryDTO dto = new EventSummaryDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setPictureURI(event.getPictureURI());
        dto.setLocation(event.getLocation());
        dto.setDate(event.getDate());
        dto.setCreationDate(event.getCreationDate());
        dto.setModificationDate(event.getModificationDate());
        dto.setCreator(this.userDTOUserMapper.mapUserToUserPublicDTO(event.getCreator()));
        dto.setIsPrivate(event.isPrivateEvent());
        dto.setParticipantCount(event.getParticipants().size());
        dto.setCommentCount(event.getComments().size());
        dto.setMediaCount(event.getMedia().size());

        return dto;
    }

    // Event → EventUpdateDTO
    public EventUpdateDTO mapEventToEventUpdateDTO(Event event) {
        if (event == null) {
            return null;
        }

        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        // Note: picture is MultipartFile, not mapped from entity
        dto.setLocation(event.getLocation());
        dto.setDate(event.getDate());
        dto.setPrivate(event.isPrivateEvent());
        // Note: modification date & creation date are set in the database, EventUpdateDTO only has the values that can be updated by the user

        // Map participants
        if (event.getParticipants() != null) {
            dto.setParticipants(userDTOUserMapper.mapUsersToUserPublicDTOs(event.getParticipants()));
        }

        return dto;
    }

    // ? Maybe we don't need this at all because updateDTO only has the fields that will be updated and we never map directly ?
    // EventUpdateDTO → Event
//    public Event mapEventUpdateDTOToEvent(EventUpdateDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        Event event = new Event();
//        event.setId(dto.getId());
//        event.setTitle(dto.getTitle());
//        event.setDescription(dto.getDescription());
//        event.setLocation(dto.getLocation());
//        event.setDate(dto.getDate());
//        event.setIsPrivate(dto.isPrivate());
//        // Note: pictureURI and relationships handled in service layer
//
//        return event;
//    }

    // ? maybe we don't need that ? Event → EventCreationDTO
//    public EventCreationDTO mapEventToEventCreationDTO(Event event) {
//        if (event == null) {
//            return null;
//        }
//
//        EventCreationDTO dto = new EventCreationDTO();
//        dto.setId(event.getId());
//        dto.setTitle(event.getTitle());
//        dto.setDescription(event.getDescription());
//        dto.setLocation(event.getLocation());
//        dto.setDate(event.getDate());
//        dto.setIsPrivate(event.isPrivate());
//        // eventPicture (MultipartFile) is ignored as specified
//
//        return dto;
//    }

    // EventCreationDTO → Event
    public Event mapEventCreationDTOToEvent(EventCreationDTO dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setDate(dto.getDate());
        event.setCreator(this.userDTOUserMapper.mapUserPublicDTOToUser(dto.getCreator()));
        event.setPrivateEvent(dto.isPrivate());
        // pictureURI is ignored and handled in service layer

        return event;
    }

    // List mappings
    public List<EventDTO> mapEventsToEventDTOs(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(this::mapEventToEventDTO)
                .collect(Collectors.toList());
    }

    public List<EventSummaryDTO> mapEventsToEventSummaryDTOs(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(this::toEventSummaryDTO)
                .collect(Collectors.toList());
    }

    public Page<EventSummaryDTO> mapEventsToEventSummaryDTOs(Page<Event> events) {
        if (events == null) {
            return Page.empty();
        }

        List<EventSummaryDTO> dtoList = events.stream()
                .map(this::toEventSummaryDTO)
                .toList();

        return new PageImpl<>(
                dtoList,
                events.getPageable(),
                events.getTotalElements()
        );
    }


    public List<Event> mapEventDTOsToEvents(List<EventDTO> eventDTOs) {
        if (eventDTOs == null) {
            return new ArrayList<>();
        }
        return eventDTOs.stream()
                .map(this::mapEventDTOToEvent)
                .collect(Collectors.toList());
    }

    public List<EventUpdateDTO> mapEventsToEventUpdateDTOs(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(this::mapEventToEventUpdateDTO)
                .collect(Collectors.toList());
    }

//    public List<Event> mapEventUpdateDTOsToEvents(List<EventUpdateDTO> eventUpdateDTOs) {
//        if (eventUpdateDTOs == null) {
//            return new ArrayList<>();
//        }
//        return eventUpdateDTOs.stream()
//                .map(this::mapEventUpdateDTOToEvent)
//                .collect(Collectors.toList());
//    }

//    public List<EventCreationDTO> mapEventsToEventCreationDTOs(List<Event> events) {
//        if (events == null) {
//            return new ArrayList<>();
//        }
//        return events.stream()
//                .map(this::mapEventToEventCreationDTO)
//                .collect(Collectors.toList());
//    }

    public List<Event> mapEventCreationDTOsToEvents(List<EventCreationDTO> eventCreationDTOs) {
        if (eventCreationDTOs == null) {
            return new ArrayList<>();
        }
        return eventCreationDTOs.stream()
                .map(this::mapEventCreationDTOToEvent)
                .collect(Collectors.toList());
    }
}
