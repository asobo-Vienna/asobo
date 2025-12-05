package at.msm.asobo.services;

import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAuthorizationService {
    private final UserRepository userRepository;

    public UserAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean canUpdateEntity(UUID targetUserId, UUID loggedInUserId) {
        if (targetUserId.equals(loggedInUserId)) {
            return true;
        }
        return hasAdminRole(loggedInUserId);
    }

    private boolean hasAdminRole(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
    }
}
