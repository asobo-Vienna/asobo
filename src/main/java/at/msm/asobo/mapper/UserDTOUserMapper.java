package at.msm.asobo.mapper;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOUserMapper {
    UserDTO mapUserToUserDTO(User user);
    User mapUserDTOToUser(UserDTO userDTO);

    List<UserDTO> mapUsersToUserDTOs(List<User> users);
    List<User> mapUserDTOsToUsers(List<UserDTO> userDTOs);
}
