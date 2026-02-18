package at.msm.asobo.mappers.helpers;

import at.msm.asobo.entities.User;
import at.msm.asobo.services.UserService;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserMapperHelper {

    private final UserService userService;

    public UserMapperHelper(UserService userService) {
        this.userService = userService;
    }

    @Named("uuidToUser")
    public User fromId(UUID id) {
        return userService.getUserByIdIncludeDeleted(id);
    }

    @Named("userToUuid")
    public UUID toId(User user) {
        return user.getId();
    }
}
