package at.msm.asobo.mappers.helpers;

import at.msm.asobo.entities.User;
import at.msm.asobo.services.UserService;
import at.msm.asobo.services.files.FileStorageService;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Component
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
