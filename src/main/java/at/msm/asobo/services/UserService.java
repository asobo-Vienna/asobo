package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.services.files.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;

    public UserService(UserRepository userRepository, UserDTOUserMapper userDTOUserMapper, FileStorageService fileStorageService, FileStorageProperties fileStorageProperties) {
        this.userRepository = userRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
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

        if (userRegisterDTO.getProfilePicture() != null && !userRegisterDTO.getProfilePicture().isEmpty()) {
            String fileURI = fileStorageService.store(userRegisterDTO.getProfilePicture(), this.fileStorageProperties.getProfilePictureSubfolder());
            newUser.setPictureURI(fileURI);
        }

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
        existingUser.setSalutation(userUpdateDTO.getSalutation());
        existingUser.setLocation(userUpdateDTO.getLocation());
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setPassword(userUpdateDTO.getPassword());

        // Handle the picture if it is present
        MultipartFile picture = userUpdateDTO.getPicture();
        if (picture != null && !picture.isEmpty()) {
            String pictureURI = fileStorageService.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
            existingUser.setPictureURI(pictureURI);
        }

        User updatedUser = userRepository.save(existingUser);
        return this.userDTOUserMapper.mapUserToUserDTO(updatedUser);
    }

    public UserDTO deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.userRepository.delete(userToDelete);
        return this.userDTOUserMapper.mapUserToUserDTO(userToDelete);
    }
}
