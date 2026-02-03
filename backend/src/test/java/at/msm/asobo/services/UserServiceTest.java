package at.msm.asobo.services;

import at.msm.asobo.builders.UserTestBuilder;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.users.UserNotFoundException;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.UserRepository;
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
    private UserDTOUserMapper userDTOUserMapper;

    @InjectMocks
    private UserService userService;

    private UUID userIdJohn;
    private UUID userIdJane;
    private User userJohn;
    private User userJane;
    private UserPublicDTO userPublicDTO;


    @BeforeEach void setup() {
        userIdJohn = UUID.randomUUID();
        userIdJane = UUID.randomUUID();

        userJohn = new UserTestBuilder()
                .withId(userIdJohn)
                .withUsername("john")
                .buildUserEntityWithFixedId(userIdJohn);

        userJane = new UserTestBuilder()
                .withId(userIdJane)
                .withUsername("jane")
                .buildUserEntityWithFixedId(userIdJane);

        userPublicDTO = new UserPublicDTO();
    }

    @Test
    void getUserById_existingUser_returnsUser() {
        when(userRepository.findById(userIdJohn)).thenReturn(Optional.of(userJohn));

        User result = userService.getUserById(userIdJohn);

        assertNotNull(result);
        assertEquals(userJohn, result);
        verify(userRepository).findById(userIdJohn);
    }

    @Test
    void getUserById_nonExistingUser_throwsException() {
        when(userRepository.findById(userIdJohn)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userIdJohn));
        verify(userRepository).findById(userIdJohn);
    }

    @Test
    void getUsersByIds_existingUsers_returnsUsers() {
        Set<UUID> ids = Set.of(userIdJohn, userIdJane);
        Set<User> users = Set.of(userJohn, userJane);
        when(userRepository.findAllByIdIn(ids)).thenReturn(users);

        Set<User> result = userService.getUsersByIds(ids);

        assertEquals(users, result);
        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void getUsersByIds_noUsersFound_returnsEmptySet() {
        Set<UUID> ids = Set.of(userIdJane);
        when(userRepository.findAllByIdIn(ids)).thenReturn(Set.of());

        Set<User> result = userService.getUsersByIds(ids);

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void getUsersByIds_emptyInput_returnsEmptySet() {
        Set<UUID> ids = Set.of();
        when(userRepository.findAllByIdIn(ids)).thenReturn(Set.of());

        Set<User> result = userService.getUsersByIds(ids);

        assertTrue(result.isEmpty());
        verify(userRepository).findAllByIdIn(ids);
    }

    @Test
    void shouldReturnDTOWhenUserExists() {
        when(userRepository.findById(userIdJohn)).thenReturn(Optional.of(userJohn));
        when(userDTOUserMapper.mapUserToUserPublicDTO(userJohn)).thenReturn(userPublicDTO);

        UserPublicDTO result = userService.getUserDTOById(userIdJohn);

        assertEquals(userPublicDTO, result);
        verify(userRepository).findById(userIdJohn);
        verify(userDTOUserMapper).mapUserToUserPublicDTO(userJohn);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(userIdJohn)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDTOById(userIdJohn));

        verify(userRepository).findById(userIdJohn);
        verify(userDTOUserMapper, never()).mapUserToUserPublicDTO(any());
    }

}