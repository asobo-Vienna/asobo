package at.msm.asobo.services;

import at.msm.asobo.dto.search.EventSearchResultDTO;
import at.msm.asobo.dto.search.GlobalSearchRequestDTO;
import at.msm.asobo.dto.search.GlobalSearchResponseDTO;
import at.msm.asobo.dto.search.UserSearchResultDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.repositories.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GlobalSearchService {

  private final EventRepository eventRepository;
  private final UserRepository userRepository;

  public GlobalSearchService(EventRepository eventRepository, UserRepository userRepository) {
    this.eventRepository = eventRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public GlobalSearchResponseDTO search(GlobalSearchRequestDTO request) {
    GlobalSearchResponseDTO response = new GlobalSearchResponseDTO();

    String query = request.getQuery();

    if (query != null && !query.trim().isEmpty()) {
      // Search events
      List<Event> events =
          eventRepository.globalSearch(
              query,
              request.getStartDate(),
              request.getEndDate(),
              request.getLocation(),
              request.getIncludePrivateEvents());

      response.setEvents(
          events.stream().map(this::mapToEventResult).limit(15).collect(Collectors.toList()));

      response.setUsers(List.of());
      response.setTotalResults(response.getEvents().size());

      if (request.getIncludeUsers()) {
        // Search users
        List<User> users = userRepository.searchUsers(query);
        response.setUsers(
            users.stream().map(this::mapToUserResult).limit(15).collect(Collectors.toList()));

        response.setTotalResults(response.getEvents().size() + response.getUsers().size());
      }
    } else {
      response.setEvents(List.of());
      response.setUsers(List.of());
      response.setTotalResults(0);
    }

    return response;
  }

  private UserSearchResultDTO mapToUserResult(User user) {
    UserSearchResultDTO result = new UserSearchResultDTO();
    result.setId(user.getId());
    result.setUsername(user.getUsername());
    result.setFirstName(user.getFirstName());
    result.setSurname(user.getSurname());
    result.setFullName(user.getFirstName() + " " + user.getSurname());
    result.setAboutMe(this.truncate(user.getAboutMe(), 150));
    result.setPictureURI(user.getPictureURI());
    result.setLocation(user.getLocation());

    if (user.getCreatedEvents() != null) {
      result.setCreatedEventsCount(user.getCreatedEvents().size());
    }

    return result;
  }

  private EventSearchResultDTO mapToEventResult(Event event) {
    EventSearchResultDTO result = new EventSearchResultDTO();
    result.setId(event.getId());
    result.setTitle(event.getTitle());
    result.setDescription(this.truncate(event.getDescription(), 200));
    result.setDate(event.getDate());
    result.setLocation(event.getLocation());
    result.setPictureURI(event.getPictureURI());
    result.setPrivateEvent(event.isPrivateEvent());

    if (event.getCreator() != null) {
      result.setCreatorName(event.getCreator().getUsername());
    }

    if (event.getParticipants() != null) {
      result.setParticipantCount(event.getParticipants().size());
    }

    return result;
  }

  private String truncate(String text, int maxLength) {
    if (text == null) return null;
    if (text.length() <= maxLength) return text;
    return text.substring(0, maxLength) + "...";
  }
}
