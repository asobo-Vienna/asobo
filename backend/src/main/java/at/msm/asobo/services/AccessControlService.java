package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import at.msm.asobo.repositories.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

  private final UserRepository userRepository;

  public AccessControlService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  //    public UUID getCurrentUserId() {
  //        Authentication authentication =
  // SecurityContextHolder.getContext().getAuthentication();
  //        if (authentication == null || !authentication.isAuthenticated()) {
  //            throw new UserNotAuthorizedException("User must be authenticated");
  //        }
  //
  //        Object principal = authentication.getPrincipal();
  //        if (principal instanceof UserPrincipal) {
  //            return ((UserPrincipal) principal).getUserId();
  //        }
  //
  //        throw new UserNotAuthenticatedException("Invalid principal type");
  //    }

  public boolean canUpdateEntity(UUID targetUserId, User loggedInUser) {
    return targetUserId.equals(loggedInUser.getId()) || this.hasAdminRole(loggedInUser);
  }

  public boolean hasAdminRole(User user) {
    return this.hasSuperadminRole(user)
        || user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
  }

  public boolean hasAdminRole(UUID userId) {
    if (userId == null) return false;
    return this.userRepository.existsByIdAndRolesName(userId, "ADMIN")
        || this.userRepository.existsByIdAndRolesName(userId, "SUPERADMIN");
  }

  private boolean hasSuperadminRole(User user) {
    return user.getRoles().stream().anyMatch(role -> role.getName().equals("SUPERADMIN"));
  }

  public void assertCanUploadMedia(Event event, User user) {
    if (!event.getParticipants().contains(user)
        && !event.getEventAdmins().contains(user)
        && !this.hasAdminRole(user)) {
      throw new UserNotAuthorizedException("You are not allowed to upload media to this event");
    }
  }

  public void assertCanDeleteMedium(Medium medium, User user) {
    Event event = medium.getEvent();
    if (!medium.getCreator().getId().equals(user.getId())
        && !event.getEventAdmins().contains(user)
        && !this.hasAdminRole(user)) {
      throw new UserNotAuthorizedException("You are not allowed to delete this media item");
    }
  }

  public void assertCanUpdateComment(UserComment comment, User user) {
    if (!comment.getAuthor().getId().equals(user.getId()) && !this.hasAdminRole(user)) {
      throw new UserNotAuthorizedException("You are not allowed to update this comment");
    }
  }

  public void assertCanDeleteComment(UserComment comment, User user) {
    Event event = comment.getEvent();
    if (!comment.getAuthor().getId().equals(user.getId())
        && !event.getEventAdmins().contains(user)
        && !this.hasAdminRole(user)) {
      throw new UserNotAuthorizedException("You are not allowed to delete this comment");
    }
  }

  public void assertCanUpdateOrDeleteUser(UUID targetUserId, User loggedInUser) {
    if (!targetUserId.equals(loggedInUser.getId()) && !this.hasAdminRole(loggedInUser)) {
      throw new UserNotAuthorizedException("You are not allowed to update or delete this user");
    }
  }
}
