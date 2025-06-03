package at.msm.asobo.services;

import at.msm.asobo.dto.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.entities.UserComment;
import at.msm.asobo.exceptions.UserCommentNotFoundException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    public User updateUserById(UUID userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existingUser.setActive(userUpdateDTO.isActive());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setLocation(userUpdateDTO.getLocation());
        existingUser.setPictureURI(userUpdateDTO.getPictureURI());
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setPassword(userUpdateDTO.getPassword());

        return userRepository.save(existingUser);
    }

    public User deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.userRepository.delete(userToDelete);
        return userToDelete;
    }
}
