package at.msm.asobo.mappers;

import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.helpers.UserMapperHelper;
import at.msm.asobo.mappers.helpers.PictureMapperHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, PictureMapperHelper.class})
public interface UserDTOUserMapper {

    UserDTO mapUserToUserDTO(User user);
    User mapUserDTOToUser(UserDTO userDTO);

    List<UserDTO> mapUsersToUserDTOs(List<User> users);
    List<User> mapUserDTOsToUsers(List<UserDTO> userDTOs);

    UserPublicDTO mapUserToUserPublicDTO(User user);
    User mapUserPublicDTOToUser(UserPublicDTO userPublicDTO);

    List<UserPublicDTO> mapUsersToUserPublicDTOs(List<User> users);
    List<User> mapUserPublicDTOsToUsers(List<UserPublicDTO> userDTOs);

    @Mapping(target = "profilePicture", ignore = true)
    UserRegisterDTO mapUserToUserRegisterDTO(User user);

    @Mapping(target = "pictureURI", ignore = true) // Ignore MultipartFile here
    User mapUserRegisterDTOToUser(UserRegisterDTO userDTO);

    List<UserRegisterDTO> mapUsersToUserRegisterDTOs(List<User> users);
    List<User> mapUserRegisterDTOsToUsers(List<UserRegisterDTO> userDTOs);

    UserFullDTO mapUserToUserFullDTO(User user);
    User mapUserFullDTOToUser(UserFullDTO userFullDTO);

    List<UserFullDTO> mapUsersToUserFullDTOs(List<User> users);
    List<User> mapUserFullDTOsToUsers(List<UserFullDTO> userDTOs);
}
