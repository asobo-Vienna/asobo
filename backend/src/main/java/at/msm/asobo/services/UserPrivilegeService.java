package at.msm.asobo.services;

import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserPrivilegeService {
    private final UserRepository userRepository;

    public UserPrivilegeService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public boolean canUpdateEntity(UUID targetUserId, UUID loggedInUserId) {
        return targetUserId.equals(loggedInUserId) || this.hasAdminRole(loggedInUserId);
    }

    private boolean hasAdminRole(UUID userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return this.hasSuperadminRole(userId) || user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }

    private boolean hasSuperadminRole(UUID userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SUPERADMIN"));
    }
}
