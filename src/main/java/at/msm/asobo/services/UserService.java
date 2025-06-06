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

    public UserDTO getUserDTOById(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return this.userDTOUserMapper.mapUserToUserDTO(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return this.userDTOUserMapper.mapUserToUserDTO(user);
    }

    public UserDTO registerUser(UserRegisterDTO userRegisterDTO) {
        User newUser = this.userDTOUserMapper.mapUserRegisterDTOToUser(userRegisterDTO);
        User savedUser = this.userRepository.save(newUser);
        return this.userDTOUserMapper.mapUserToUserDTO(savedUser);
    }

    // in case we might need it later
    public UserDTO createUser(UserDTO userDTO) {
        User user = this.userDTOUserMapper.mapUserDTOToUser(userDTO);
        User newUser = this.userRepository.save(user);
        return this.userDTOUserMapper.mapUserToUserDTO(newUser);
    }

    public UserDTO updateUserById(UUID userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = this.getUserById(userId);

        existingUser.setActive(userUpdateDTO.isActive());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setLocation(userUpdateDTO.getLocation());
        existingUser.setPictureURI(userUpdateDTO.getPictureURI());
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setPassword(userUpdateDTO.getPassword());
        User updatedUser = userRepository.save(existingUser);
        return this.userDTOUserMapper.mapUserToUserDTO(updatedUser);
    }

    public UserDTO deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.userRepository.delete(userToDelete);
        return this.userDTOUserMapper.mapUserToUserDTO(userToDelete);
    }
}
