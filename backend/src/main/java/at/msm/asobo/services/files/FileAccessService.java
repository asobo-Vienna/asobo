package at.msm.asobo.services.files;

import at.msm.asobo.entities.Event;
import at.msm.asobo.services.AccessControlService;
import at.msm.asobo.services.events.EventAdminService;
import at.msm.asobo.services.events.EventService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FileAccessService {

  private final EventService eventService;
  private final AccessControlService accessControlService;
  private final EventAdminService eventAdminService;

  public FileAccessService(
      EventService eventService,
      AccessControlService accessControlService,
      EventAdminService eventAdminService) {
    this.eventService = eventService;
    this.eventAdminService = eventAdminService;
    this.accessControlService = accessControlService;
  }

  public boolean canAccess(String filepath, UUID userId) {
    // Profile pictures → only visible for logged in users
    if (filepath.contains("profile-pictures")) {
      return userId != null;
    }

    // Event pictures
    if (filepath.contains("event-cover-pictures")) {
      if (userId != null && this.accessControlService.hasAdminRole(userId)) {
        return true;
      }

      Event event = this.findEventByPicturePath(filepath);

      if (!event.getIsPrivateEvent()) {
        return true; // Public event
      }

      if (userId == null) {
        return false; // Private event & not logged in
      }

      if (this.eventAdminService.canManageEvent(event, userId)) {
        return true;
      }

      return this.isParticipant(event, userId);
    }
    return true;
  }

  public Event findEventByPicturePath(String filepath) {
    return this.eventService.getEventByPicturePath(filepath);
  }

  private boolean isParticipant(Event event, UUID userId) {
    return event.getParticipants().stream().anyMatch(p -> p.getId().equals(userId))
        || event.getCreator().getId().equals(userId)
        || event.getEventAdmins().stream().anyMatch(a -> a.getId().equals(userId));
  }
}
