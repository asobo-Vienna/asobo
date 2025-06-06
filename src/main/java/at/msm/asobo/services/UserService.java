package at.msm.asobo.services;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.mapper.UserDTOUserMapper;
import at.msm.asobo.mapper.UserDTOUserMapperImpl;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDTOUserMapper userDTOUserMapper;

    public UserService(UserRepository userRepository, UserDTOUserMapper userDTOUserMapper) {
        this.userRepository = userRepository;
        this.userDTOUserMapper = userDTOUserMapper;
    }

    public List<UserDTO> getAllUsers() {
        return this.userDTOUserMapper.mapUsersToUserDTOs(this.userRepository.findAll());
    }

    public User getUserById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserDTO registerUser(UserRegisterDTO userRegisterDTO) {
        User newUser = new User(userRegisterDTO);
        User savedUser = this.userRepository.save(newUser);
        return new UserDTO(savedUser);
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }


    public UserDTO updateUserById(UUID userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existingUser.setActive(userUpdateDTO.isActive());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setLocation(userUpdateDTO.getLocation());
        existingUser.setPictureURI(userUpdateDTO.getPictureURI());
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setPassword(userUpdateDTO.getPassword());
        User updatedUser = userRepository.save(existingUser);
        return new UserDTO(updatedUser);
    }

    public User deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.userRepository.delete(userToDelete);
        return userToDelete;
    }
}
