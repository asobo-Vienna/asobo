package at.msm.asobo.services;

import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.auth.LoginResponseDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    AccessControlService accessControlService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserDTOUserMapper userDTOUserMapper;

    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User userJohn;
    private User userJane;
    private UserPublicDTO userPublicDTO;
    private UserDTO userDTO;
    private UserPrincipal principalJohn;
    private UserPrincipal principalJane;

    @BeforeEach void setup() {
        userJohn = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("john")
                .buildUserEntity();

        userJane = new UserTestBuilder()
                .withId(UUID.randomUUID())
                .withUsernameAndEmail("jane")
                .buildUserEntity();

        userPublicDTO = new UserPublicDTO();
        userDTO = new UserDTO();

        principalJohn = new UserTestBuilder()
                .fromUser(userJohn)
                .buildUserPrincipal();

        principalJane = new UserTestBuilder()
                .fromUser(userJane)
                .buildUserPrincipal();
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        when(userRepository.findById(userJohn.getId())).thenReturn(Optional.of(userJohn));

        User result = userService.getUserById(userJohn.getId());

        assertNotNull(result);
        assertEquals(userJohn, result);

        verify(userRepository).findById(userJohn.getId());
    }

    @Test
    void getUserById_nonExistingUser_throwsException() {
        when(userRepository.findById(userJohn.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userJohn.getId()));

        verify(userRepository).findById(userJohn.getId());
    }

    @Test
    void getUsersByIds_existingUsers_returnsUsers() {
        Set<UUID> ids = Set.of(userJohn.getId(), userJane.getId());
        Set<User> users = Set.of(userJohn, userJane);

        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        Set<User> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertEquals(users, result);

        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void getUsersByIds_noUsersFound_returnsEmptySet() {
        Set<UUID> ids = Set.of(userJane.getId());

        when(userRepository.findAllByIdIn(ids)).thenReturn(Set.of());

        Set<User> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void getUsersByIds_emptyInput_returnsEmptySet() {
        Set<UUID> ids = Set.of();

        when(userRepository.findAllByIdIn(ids)).thenReturn(Set.of());

        Set<User> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void getUserDTOById_returnsDTOWhenUserExists() {
        when(userRepository.findById(userJohn.getId())).thenReturn(Optional.of(userJohn));
        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn)).thenReturn(userPublicDTO);

        UserPublicDTO result = userService.getUserDTOById(userJohn.getId());

        assertNotNull(result);
        assertEquals(userPublicDTO, result);

        verify(userRepository).findById(userJohn.getId());
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void getUserDTOById_throwsExceptionWhenUserNotFound() {
        when(userRepository.findById(userJohn.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDTOById(userJohn.getId()));

        verify(userRepository).findById(userJohn.getId());
        verify(userDTOUserMapper, never()).mapUserToUserPublicDTO(any());
    }

    @Test
    void getUserByUsername_returnsUserDTOWhenUserExists() {
        String username = "Existent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userJohn));
        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn)).thenReturn(userPublicDTO);

        UserPublicDTO result = userService.getUserByUsername(username);

        assertNotNull(result);
        assertEquals(userPublicDTO, result);

        verify(userRepository).findByUsername(username);
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void getUserByUsername_throwsExceptionWhenUserNotFound() {
        String username = "Nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(username));

        verify(userRepository).findByUsername(username);
        verify(userDTOUserMapper, never()).mapUserToUserPublicDTO(any());
    }

    @Test
    void createUser_returnsDTOWhenSuccessful() {
        when(userDTOUserMapper.mapUserDTOToUser(userDTO)).thenReturn(userJohn);
        when(userRepository.save(userJohn)).thenReturn(userJohn);
        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn)).thenReturn(userPublicDTO);

        UserPublicDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(userPublicDTO, result);

        verify(userDTOUserMapper).mapUserDTOToUser(userDTO);
        verify(userRepository).save(userJohn);
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void saveUser_returnsUserWhenSuccessful() {
        when(userRepository.save(userJohn)).thenReturn(userJohn);

        User result = userService.saveUser(userJohn);

        assertNotNull(result);
        assertEquals(userJohn, result);

        verify(userRepository).save(userJohn);
    }

    @Test
    void updateUserById_usernameNotChanged_doesNotGenerateToken() {

        when(userRepository.findById(userJane.getId()))
                .thenReturn(Optional.of(userJane));

        when(userRepository.save(userJane))
                .thenReturn(userJane);

        when(userDTOUserMapper.mapUserToUserPublicDTO(userJane))
                .thenReturn(userPublicDTO);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername(userJane.getUsername());

        LoginResponseDTO result =
                userService.updateUserById(userJane.getId(), principalJane, dto);

        assertNotNull(result);
        assertNotNull(result.getUserDTO());
        assertNull(result.getToken());

        verify(jwtUtil, never()).generateToken(any(UserPrincipal.class), anyLong());
        verify(userRepository, times(2)).findById(userJane.getId());
        verify(accessControlService).assertCanUpdateOrDeleteUser(userJane.getId(), userJane);
        verify(userRepository).save(userJane);
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJane);
    }

    @Test
    void updateUserById_usernameChanged_generatesToken() {
        when(userRepository.findById(userJohn.getId()))
                .thenReturn(Optional.of(userJohn));

        when(userRepository.save(userJohn))
                .thenReturn(userJohn);

        when(jwtUtil.generateToken(any(UserPrincipal.class), anyLong()))
                .thenReturn("token");

        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn))
                .thenReturn(userPublicDTO);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("newUsername");

        LoginResponseDTO result =
                userService.updateUserById(userJohn.getId(), principalJohn, dto);

        assertNotNull(result);
        assertEquals("token", result.getToken());

        verify(userRepository, times(2)).findById(userJohn.getId());
        verify(accessControlService).assertCanUpdateOrDeleteUser(userJohn.getId(), userJohn);
        verify(userRepository).save(userJohn);
        verify(jwtUtil).generateToken(any(UserPrincipal.class), anyLong());
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void deleteUserById_withValidCredentials_deletesUser() {
        when(userRepository.findById(userJohn.getId()))
                .thenReturn(Optional.of(userJohn));

        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn))
                .thenReturn(userPublicDTO);

        UserPublicDTO result =
                userService.deleteUserById(userJohn.getId(), principalJohn);

        assertNotNull(result);
        assertEquals(userPublicDTO, result);

        verify(userRepository, times(2)).findById(userJohn.getId());
        verify(accessControlService).assertCanUpdateOrDeleteUser(userJohn.getId(), userJohn);
        verify(userRepository).delete(userJohn);
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void isUserNameAlreadyTaken_returnsFalseForNewUsername() {
        String username = "NewUsername";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean result = userService.isUsernameAlreadyTaken(username);

        assertFalse(result);

        verify(userRepository).existsByUsername(username);
    }

    @Test
    void isUsernameAlreadyTaken_returnsTrueForExistingUsername() {
        String username = "ExistentUsername";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean result = userService.isUsernameAlreadyTaken(username);

        assertTrue(result);

        verify(userRepository).existsByUsername(username);
    }

    @Test
    void isEmailAlreadyTaken_returnsFalseForNewUsername() {
        String email = "newUsername@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = userService.isEmailAlreadyTaken(email);

        assertFalse(result);

        verify(userRepository).existsByEmail(email);
    }

    @Test
    void isEmailAlreadyTaken_returnsTrueForExistingUsername() {
        String email = "existingUsername@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = userService.isEmailAlreadyTaken(email);

        assertTrue(result);

        verify(userRepository).existsByEmail(email);
    }
}