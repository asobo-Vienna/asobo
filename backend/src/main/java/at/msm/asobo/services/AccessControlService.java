package at.msm.asobo.services;

import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.Medium;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.users.UserNotAuthorizedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccessControlService {

    public AccessControlService() {
    }


//    public UUID getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
        return this.hasSuperadminRole(user) || user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }

    private boolean hasSuperadminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SUPERADMIN"));
    }

    public void assertCanUploadMedia(Event event, User user) {
        if (!event.getParticipants().contains(user)) {
            throw new UserNotAuthorizedException(
                    "You are not allowed to upload media to this event because you are not a participant"
            );
        }
    }

    public void assertCanDeleteMedium(Medium medium, User user) {
        if (!medium.getCreator().getId().equals(user.getId()) && !this.hasAdminRole(user)) {
            throw new UserNotAuthorizedException(
                    "You are not allowed to delete media you did not create"
            );
        }
    }

    public void assertCanUpdateComment(UserComment comment, User user) {
        if (!comment.getAuthor().getId().equals(user.getId()) && !this.hasAdminRole(user)) {
            throw new UserNotAuthorizedException("You are not allowed to update a comment you did not create");
        }
    }

    public void assertCanDeleteComment(UserComment comment, User user) {
        if (!comment.getAuthor().getId().equals(user.getId()) && !this.hasAdminRole(user)) {
            throw new UserNotAuthorizedException("You are not authorized to delete a comment you did not create");
        }
    }

    public void assertCanUpdateOrDeleteUser(User targetUser, User loggedInUser) {
        if (!targetUser.getId().equals(loggedInUser.getId()) && !hasAdminRole(loggedInUser)) {
            throw new UserNotAuthorizedException("You are not allowed to update or delete this user");
        }
    }
}
