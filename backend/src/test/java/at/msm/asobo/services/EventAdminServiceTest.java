package at.msm.asobo.services;

import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.entities.Event;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.EventDTOEventMapper;
import at.msm.asobo.mappers.UserDTOUserMapper;
import at.msm.asobo.repositories.EventRepository;
import at.msm.asobo.security.UserPrincipal;
import at.msm.asobo.services.events.EventAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventAdminServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @Mock
    private AccessControlService accessControlService;

    @Mock
    private UserDTOUserMapper userDTOUserMapper;

    @Mock
    private EventDTOEventMapper eventDTOEventMapper;

    @InjectMocks
    private EventAdminService eventAdminService;

    private UUID eventId;
    private UUID userId;
    private User user;
    private Event event;
    private UserPrincipal userPrincipal;
    private Set<User> eventAdmins;
    private Set<UserPublicDTO> eventAdminDTOs;

    @BeforeEach
    void setUp() {
        this.eventId = UUID.randomUUID();
        this.userId = UUID.randomUUID();

        this.user = new User();
        this.user.setId(this.userId);
        this.user.setEmail("test@example.com");
        this.user.setFirstName("Test");
        this.user.setSurname("User");

        this.event = new Event();
        this.event.setId(this.eventId);
        this.eventAdmins = new HashSet<>();
        this.event.setEventAdmins(this.eventAdmins);

        this.userPrincipal = new UserPrincipal(this.userId, "test@example.com", "password", null);

        this.eventAdminDTOs = new HashSet<>();
        UserPublicDTO dto = new UserPublicDTO();
        dto.setId(this.userId);
        dto.setEmail("test@example.com");
        this.eventAdminDTOs.add(dto);
    }

    @Test
    void getAllEventAdminsByEventId_returnsAdminDTOs() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userDTOUserMapper.mapUsersToUserPublicDTOs(eventAdmins))
                .thenReturn(eventAdminDTOs);

        Set<UserPublicDTO> result = eventAdminService.getAllEventAdminsByEventId(eventId);

        assertNotNull(result);
        assertEquals(eventAdminDTOs, result);
        verify(eventRepository).findById(eventId);
        verify(userDTOUserMapper).mapUsersToUserPublicDTOs(eventAdmins);
    }
}
