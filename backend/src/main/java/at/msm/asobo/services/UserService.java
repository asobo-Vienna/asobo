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
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.utils.PatchUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Value("${jwt.expiration-ms}")
    private long EXPIRATION_MS;

    @Value("${jwt.remember-me-expiration-ms}")
    private long REMEMBER_ME_EXPIRATION_MS;

    private final UserRepository userRepository;
    private final UserDTOUserMapper userDTOUserMapper;
    private final FileStorageService fileStorageService;
    private final FileStorageProperties fileStorageProperties;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       UserDTOUserMapper userDTOUserMapper,
                       FileStorageService fileStorageService,
                       FileStorageProperties fileStorageProperties,
                       PasswordService passwordService,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.userDTOUserMapper = userDTOUserMapper;
        this.fileStorageService = fileStorageService;
        this.fileStorageProperties = fileStorageProperties;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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
        boolean emailExists = this.isEmailAlreadyTaken(userRegisterDTO.getEmail());
        if (emailExists) {
            throw new EmailAlreadyExistsException(userRegisterDTO.getEmail());
        }

        boolean userExists = this.isUsernameAlreadyTaken(userRegisterDTO.getUsername());
        if (userExists) {
            throw new UsernameAlreadyExistsException(userRegisterDTO.getUsername());
        }

        User newUser = this.userDTOUserMapper.mapUserRegisterDTOToUser(userRegisterDTO);

        String hashedPassword = this.passwordService.hashPassword(userRegisterDTO.getPassword());
        newUser.setPassword(hashedPassword);

        if (userRegisterDTO.getProfilePicture() != null && !userRegisterDTO.getProfilePicture().isEmpty()) {
            String fileURI = this.fileStorageService.store(userRegisterDTO.getProfilePicture(), this.fileStorageProperties.getProfilePictureSubfolder());
            newUser.setPictureURI(fileURI);
        }

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

        // Get the authenticated principal
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

    public UserPublicDTO updateUserById(UUID userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = this.getUserById(userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID authenticatedUserId = UUID.fromString(userPrincipal.getUserId());

        // Authorization check: user can only update their own profile
        if (!existingUser.getId().equals(authenticatedUserId)) {
            throw new UserNotAuthorizedException("You can only update your own profile");
        }

        // Use this instead of checking non-null for each property of the dto
        // Ignore 'profilePicture' since we handle it separately
        PatchUtils.copyNonNullProperties(userUpdateDTO, existingUser, "profilePicture");

        /*existingUser.setActive(userUpdateDTO.isActive());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setSalutation(userUpdateDTO.getSalutation());
        existingUser.setLocation(userUpdateDTO.getLocation());
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setFirstName(userUpdateDTO.getFirstName());
        existingUser.setSurname(userUpdateDTO.getSurname());
        existingUser.setPassword(userUpdateDTO.getPassword());
        existingUser.setAboutMe(userUpdateDTO.getAboutMe());*/

        // Handle the picture if it is present
        MultipartFile picture = userUpdateDTO.getProfilePicture();
        if (picture != null && !picture.isEmpty()) {
            String pictureURI = this.fileStorageService.store(picture, this.fileStorageProperties.getProfilePictureSubfolder());
            existingUser.setPictureURI(pictureURI);
        }

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
}
