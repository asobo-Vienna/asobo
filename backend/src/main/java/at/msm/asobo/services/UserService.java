package at.msm.asobo.services;

import at.msm.asobo.config.FileStorageProperties;
import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.auth.UserLoginDTO;
import at.msm.asobo.dto.user.*;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotAuthorizedException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.exceptions.registration.EmailAlreadyExistsException;
import at.msm.asobo.exceptions.registration.UsernameAlreadyExistsException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import at.msm.asobo.entities.Role;

@Service
public class UserService {
    @Value("${jwt.expiration-ms}")
    private long EXPIRATION_MS;
    @Value("${jwt.remember-me-expiration-ms}")
    private long REMEMBER_ME_EXPIRATION_MS;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final MultipartProperties multipartProperties;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserDTOUserMapper userDTOUserMapper,
                       FileStorageService fileStorageService,
                       FileStorageProperties fileStorageProperties,
                       PasswordService passwordService,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       MultipartProperties multipartProperties
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.multipartProperties = multipartProperties;
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

    public LoginResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        validateUserRegistration(userRegisterDTO);

        User newUser = this.userDTOUserMapper.mapUserRegisterDTOToUser(userRegisterDTO);

        String hashedPassword = this.passwordService.hashPassword(userRegisterDTO.getPassword());
        newUser.setPassword(hashedPassword);

        this.handleProfilePictureUpload(userRegisterDTO.getProfilePicture(), newUser);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        newUser.setRoles(Set.of(userRole));

        User savedUser = this.userRepository.save(newUser);

        UserPrincipal userPrincipal = new UserPrincipal(
                savedUser.getId().toString(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtUtil.generateToken(userPrincipal, EXPIRATION_MS);

        return new LoginResponseDTO(token, this.userDTOUserMapper.mapUserToUserPublicDTO(savedUser));
    }

    public LoginResponseDTO loginUser(UserLoginDTO userLoginDTO) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getIdentifier(),
                        userLoginDTO.getPassword()
                );
        
        Authentication authentication = authenticationManager.authenticate(authToken);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        long expirationTime = EXPIRATION_MS;
        if (userLoginDTO.isRememberMe()) {
            expirationTime = REMEMBER_ME_EXPIRATION_MS; // 30 days;
        }

        String token = jwtUtil.generateToken(userPrincipal, expirationTime);

        User user = userRepository.findById(UUID.fromString(userPrincipal.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserPublicDTO userPublicDTO = this.userDTOUserMapper.mapUserToUserPublicDTO(user);

        return new LoginResponseDTO(token, userPublicDTO);
    }

    // in case we might need it later
    public UserPublicDTO createUser(UserDTO userDTO) {
        User user = this.userDTOUserMapper.mapUserDTOToUser(userDTO);
        User newUser = this.userRepository.save(user);
        return this.userDTOUserMapper.mapUserToUserPublicDTO(newUser);
    }

    public UserPublicDTO updateUserById(UUID targetUserId, UUID loggedInUserId, UserUpdateDTO userUpdateDTO) {
        User existingUser = this.getUserById(targetUserId);

        // Authorization check: user can update their own profile or admin can update any profile
        if (!canUpdateUser(targetUserId, loggedInUserId)) {
            throw new UserNotAuthorizedException("You are not authorized to update this profile");
        }

        // Use this instead of checking non-null for each property of the dto
        // Ignore 'profilePicture' since we handle it separately
        PatchUtils.copyNonNullProperties(userUpdateDTO, existingUser, "profilePicture");

        if(existingUser.getPassword() != null) {
            String hashedPassword = this.passwordService.hashPassword(existingUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }

        this.handleProfilePictureUpdate(userUpdateDTO.getProfilePicture(), existingUser);

        User updatedUser = this.userRepository.save(existingUser);
        return this.userDTOUserMapper.mapUserToUserPublicDTO(updatedUser);
    }

    public UserPublicDTO deleteUserById(UUID id) {
        User userToDelete = this.getUserById(id);
        this.fileStorageService.delete(userToDelete.getPictureURI());
        this.userRepository.delete(userToDelete);

        return this.userDTOUserMapper.mapUserToUserPublicDTO(userToDelete);
    }

    public boolean isUsernameAlreadyTaken(String username) {
        return this.userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailAlreadyTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean canUpdateUser(UUID targetUserId, UUID loggedInUserId) {
        if (targetUserId.equals(loggedInUserId)) {
            return true;
        }

        return hasAdminRole(loggedInUserId);
    }

    private boolean hasAdminRole(UUID userId) {
        // TODO: Implement when roles are added
        // User user = getUserById(userId);
        // return user.getRoles().stream()
        //     .anyMatch(role -> role.getName().equals("ADMIN"));
        return false;
    }

    private void validateUserRegistration(UserRegisterDTO userRegisterDTO) {
        if (this.isEmailAlreadyTaken(userRegisterDTO.getEmail())) {
            throw new EmailAlreadyExistsException(userRegisterDTO.getEmail());
        }

        if (this.isUsernameAlreadyTaken(userRegisterDTO.getUsername())) {
            throw new UsernameAlreadyExistsException(userRegisterDTO.getUsername());
        }
    }

    private void handleProfilePictureUpload(MultipartFile picture, User user) {
        if (picture != null && !picture.isEmpty()) {
            validateProfilePicture(picture);
            String fileURI = this.fileStorageService.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
            user.setPictureURI(fileURI);
        }
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
