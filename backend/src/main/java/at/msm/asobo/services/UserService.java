package at.msm.asobo.services;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserBasicDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.security.JwtUtil;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.files.FileStorageService;
import at.msm.asobo.utils.PatchUtils;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Value("${jwt.expiration-ms}")
  private long expirationMs;

  private final UserRepository userRepository;
  private final UserDTOUserMapper userDTOUserMapper;
  private final FileStorageService fileStorageService;
  private final PasswordService passwordService;
  private final JwtUtil jwtUtil;
  private final AccessControlService accessControlService;

  public UserService(
      UserRepository userRepository,
      UserDTOUserMapper userDTOUserMapper,
      FileStorageService fileStorageService,
      PasswordService passwordService,
      JwtUtil jwtUtil,
      AccessControlService accessControlService) {
    this.userRepository = userRepository;
    this.userDTOUserMapper = userDTOUserMapper;
    this.fileStorageService = fileStorageService;
    this.passwordService = passwordService;
    this.jwtUtil = jwtUtil;
    this.accessControlService = accessControlService;
  }

  public Set<UserBasicDTO> getAllUsersBasic() {
    return this.userDTOUserMapper.mapUsersToUserBasicDTOs(
        this.userRepository.findAllByIsDeletedFalse());
  }

  public User getUserById(UUID id) {
    return this.userRepository
        .findUserByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public User getUserByIdIncludeDeleted(UUID id) {
    return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
  }

  public Set<User> getUsersByIds(Set<UUID> ids) {
    return this.userRepository.findAllByIdInAndIsDeletedFalse(ids);
  }

  public Set<User> getUsersByIdsIncludeDeleted(Set<UUID> ids) {
    return this.userRepository.findAllByIdIn(ids);
  }

  public UserPublicDTO getUserDTOById(UUID id) {
    User user =
        this.userRepository
            .findUserByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    return this.userDTOUserMapper.mapUserToUserPublicDTO(user);
  }

  public UserPublicDTO getUserByUsername(String username) {
    User user =
        this.userRepository
            .findByUsernameAndIsDeletedFalse(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
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

  public LoginResponseDTO updateUserById(
      UUID targetUserId, UserPrincipal loggedInPrincipal, UserUpdateDTO userUpdateDTO) {
    User loggedInUser = this.getUserById(loggedInPrincipal.getUserId());
    User targetUser = this.getUserById(targetUserId);

    this.accessControlService.assertCanUpdateOrDeleteUser(targetUserId, loggedInUser);

    boolean usernameChanged =
        userUpdateDTO.getUsername() != null
            && !userUpdateDTO.getUsername().equals(targetUser.getUsername());

    PatchUtils.copyNonNullProperties(
        userUpdateDTO, targetUser, "profilePicture", "password", "isActive");

    if (userUpdateDTO.getPassword() != null) {
      this.passwordService.validatePasswordFormat(userUpdateDTO.getPassword());
      String hashedPassword = this.passwordService.hashPassword(userUpdateDTO.getPassword());
      targetUser.setPassword(hashedPassword);
    }

    this.fileStorageService.handleProfilePictureUpdate(
        userUpdateDTO.getProfilePicture(), targetUser);

    User updatedUser = this.userRepository.save(targetUser);

    if (usernameChanged) {
      UserPrincipal userPrincipal =
          new UserPrincipal(
              updatedUser.getId(),
              updatedUser.getUsername(),
              updatedUser.getPassword(),
              updatedUser.getRoles().stream()
                  .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                  .toList());

      String newToken = jwtUtil.generateToken(userPrincipal, expirationMs);
      UserPublicDTO userPublicDTO = this.userDTOUserMapper.mapUserToUserPublicDTO(updatedUser);

      return new LoginResponseDTO(newToken, userPublicDTO);
    }

    return new LoginResponseDTO(null, this.userDTOUserMapper.mapUserToUserPublicDTO(updatedUser));
  }

  public UserPublicDTO deleteUserById(UUID userToDeleteId, UserPrincipal userPrincipal) {
    User loggedInUser = this.getUserById(userPrincipal.getUserId());
    User userToDelete = this.getUserById(userToDeleteId);

    this.accessControlService.assertCanUpdateOrDeleteUser(userToDeleteId, loggedInUser);

    userToDelete.setIsDeleted(true);
    userToDelete.setIsActive(false);

    this.userRepository.save(userToDelete);

    //        if (userToDelete.getPictureURI() != null) {
    //            this.fileStorageService.delete(userToDelete.getPictureURI());
    //        }

    return this.userDTOUserMapper.mapUserToUserPublicDTO(userToDelete);
  }

  public UserPublicDTO reactivateUserById(UUID userToReactivateId, UserPrincipal userPrincipal) {
    User loggedInUser = this.getUserById(userPrincipal.getUserId());
    User userToReactivate = this.getUserByIdIncludeDeleted(userToReactivateId);

    this.accessControlService.assertCanUpdateOrDeleteUser(userToReactivateId, loggedInUser);

    userToReactivate.setIsDeleted(false);
    userToReactivate.setIsActive(true);

    this.userRepository.save(userToReactivate);

    return this.userDTOUserMapper.mapUserToUserPublicDTO(userToReactivate);
  }

  public boolean isUsernameAlreadyTaken(String username) {
    return this.userRepository.existsByUsername(username);
  }

  public boolean isEmailAlreadyTaken(String email) {
    return this.userRepository.existsByEmail(email);
  }
}
