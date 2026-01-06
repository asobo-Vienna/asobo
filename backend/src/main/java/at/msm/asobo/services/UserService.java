package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.*;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotAuthorizedException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.RoleRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.utils.PatchUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
public class UserService {
    @Value("${jwt.expiration-ms}")
    private long EXPIRATION_MS;

    private final UserRepository userRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;
    private final MultipartProperties multipartProperties;
    private final UserAuthorizationService userAuthorizationService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserDTOUserMapper userDTOUserMapper,
                       FileStorageService fileStorageService,
                       FileStorageProperties fileStorageProperties,
                       PasswordService passwordService,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       MultipartProperties multipartProperties,
                       UserAuthorizationService userAuthorizationService
    ) {
        this.userRepository = userRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
        this.multipartProperties = multipartProperties;
        this.userAuthorizationService = userAuthorizationService;
    }

    public List<UserPublicDTO> getAllUsers() {
        return this.userDTOUserMapper.mapUsersToUserPublicDTOs(this.userRepository.findAll());
    }

    public User getUserById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserPublicDTO getUserDTOById(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return this.userDTOUserMapper.mapUserToUserPublicDTO(user);
    }

    public UserPublicDTO getUserByUsername(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        return this.userDTOUserMapper.mapUserToUserPublicDTO(user);
    }

    // in case we might need it later
    public UserPublicDTO createUser(UserDTO userDTO) {
        User user = this.userDTOUserMapper.mapUserDTOToUser(userDTO);
        User newUser = this.userRepository.save(user);
        return this.userDTOUserMapper.mapUserToUserPublicDTO(newUser);
    }

    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public LoginResponseDTO updateUserById(UUID targetUserId, UUID loggedInUserId, UserUpdateDTO userUpdateDTO) {
        User existingUser = this.getUserById(targetUserId);
        boolean canUpdateUser = userAuthorizationService.canUpdateEntity(targetUserId, loggedInUserId);

        if (!canUpdateUser) {
            throw new UserNotAuthorizedException("You are not authorized to update this profile");
        }

        boolean usernameChanged = userUpdateDTO.getUsername() != null
                && !userUpdateDTO.getUsername().equals(existingUser.getUsername());

        PatchUtils.copyNonNullProperties(userUpdateDTO, existingUser, "profilePicture", "password");

        if(userUpdateDTO.getPassword() != null) {
            this.passwordService.validatePasswordFormat(userUpdateDTO.getPassword());
            String hashedPassword = this.passwordService.hashPassword(userUpdateDTO.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        this.handleProfilePictureUpdate(userUpdateDTO.getProfilePicture(), existingUser);

        User updatedUser = this.userRepository.save(existingUser);

        if (usernameChanged) {
            UserPrincipal userPrincipal = new UserPrincipal(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getPassword(),
                    updatedUser.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                            .toList()
            );

            String newToken = jwtUtil.generateToken(userPrincipal, EXPIRATION_MS);
            UserPublicDTO userPublicDTO = this.userDTOUserMapper.mapUserToUserPublicDTO(updatedUser);

            return new LoginResponseDTO(newToken, userPublicDTO);
        }

        return new LoginResponseDTO(null, this.userDTOUserMapper.mapUserToUserPublicDTO(updatedUser));
    }

    public UserPublicDTO deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.fileStorageService.delete(userToDelete.getPictureURI());
        this.userRepository.delete(userToDelete);

        return this.userDTOUserMapper.mapUserToUserPublicDTO(userToDelete);
    }

    public boolean isUsernameAlreadyTaken(String username) {
        return this.userRepository.existsByUsername(username);
    }

    public boolean isEmailAlreadyTaken(String email) {
        return this.userRepository.existsByEmail(email);
    }

    private void handleProfilePictureUpdate(MultipartFile picture, User user) {
        if (picture == null || picture.isEmpty()) {
            return;
        }

        validateProfilePicture(picture);

        if (user.getPictureURI() != null) {
            this.fileStorageService.delete(user.getPictureURI());
        }

        String pictureURI = this.fileStorageService.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
        user.setPictureURI(pictureURI);
    }

    private void validateProfilePicture(MultipartFile file) {
        long maxBytes = multipartProperties.getMaxFileSize().toBytes();
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("Profile picture must be less than " + multipartProperties.getMaxFileSize());
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed for profile pictures");
        }
    }
}
