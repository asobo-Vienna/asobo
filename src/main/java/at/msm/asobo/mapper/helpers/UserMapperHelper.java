package at.msm.asobo.mapper.helpers;

import at.msm.asobo.entities.User;
import at.msm.asobo.services.UserService;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class UserMapperHelper {
    @Autowired // consider removing this
    private UserService userService;

    @Named("uuidToUser")
    public User fromId(UUID id) {
        return userService.getUserById(id);
    }

    @Named("userToUuid")
    public UUID toId(User user) {
        return user.getId();
    }
}
