package at.msm.asobo.mapper;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.mapper.helpers.UserMapperHelper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class})
public interface UserDTOUserMapper {
    UserDTO mapUserToUserDTO(User user);
    User mapUserDTOToUser(UserDTO userDTO);
    List<UserDTO> mapUsersToUserDTOs(List<User> users);
    List<User> mapUserDTOsToUsers(List<UserDTO> userDTOs);

    UserRegisterDTO mapUserToUserRegisterDTO(User user);
    User mapUserRegisterDTOToUser(UserRegisterDTO userDTO);
    List<UserRegisterDTO> mapUsersToUserRegisterDTOs(List<User> users);
    List<User> mapUserRegisterDTOsToUsers(List<UserRegisterDTO> userDTOs);
}
