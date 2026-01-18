package at.msm.asobo.mappers;

import at.msm.asobo.dto.auth.UserRegisterDTO;
import at.msm.asobo.dto.user.*;
import at.msm.asobo.entities.User;
import at.msm.asobo.mappers.helpers.UserMapperHelper;
import at.msm.asobo.mappers.helpers.PictureMapperHelper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {UserMapperHelper.class, PictureMapperHelper.class})
public interface UserDTOUserMapper {

    UserDTO mapUserToUserDTO(User user);
    User mapUserDTOToUser(UserDTO userDTO);

    List<UserDTO> mapUsersToUserDTOs(List<User> users);
    List<User> mapUserDTOsToUsers(List<UserDTO> userDTOs);

    UserPublicDTO mapUserToUserPublicDTO(User user);
    User mapUserPublicDTOToUser(UserPublicDTO userPublicDTO);

    Set<UserPublicDTO> mapUsersToUserPublicDTOs(Set<User> users);
    Set<User> mapUserPublicDTOsToUsers(Set<UserPublicDTO> userDTOs);
    List<UserPublicDTO> mapUsersToUserPublicDTOsAsList(List<User> users);
    List<UserFullDTO> mapUsersToUserFullDTOsAsList(List<User> users);

    UserRegisterDTO mapUserToUserRegisterDTO(User user);

    User mapUserRegisterDTOToUser(UserRegisterDTO userDTO);

    List<UserRegisterDTO> mapUsersToUserRegisterDTOs(List<User> users);
    List<User> mapUserRegisterDTOsToUsers(List<UserRegisterDTO> userDTOs);

    UserFullDTO mapUserToUserFullDTO(User user);

    @Mapping(target = "roles", ignore = true)
    User mapUserFullDTOToUser(UserFullDTO userFullDTO);

    List<UserFullDTO> mapUsersToUserFullDTOs(List<User> users);

    @Mapping(target = "roles", ignore = true)
    List<User> mapUserFullDTOsToUsers(List<UserFullDTO> userFullDTOs);

    @Mapping(target = "createdEventsCount", expression = "java(user.getCreatedEvents() != null ? user.getCreatedEvents().size() : 0)")
    @Mapping(target = "attendedEventsCount", expression = "java(user.getAttendedEvents() != null ? user.getAttendedEvents().size() : 0)")
    @Mapping(target = "commentsCount", expression = "java(user.getComments() != null ? user.getComments().size() : 0)")
    UserAdminSummaryDTO mapUserToUserAdminSummaryDTO(User user);

    @Mapping(target = "createdEvents", ignore = true)
    @Mapping(target = "attendedEvents", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User mapUserAdminSummaryDTOToUser(UserAdminSummaryDTO userAdminSummaryDTO);

    default Page<UserAdminSummaryDTO> mapUsersToAdminSummaryDTOs(Page<User> users) {
        return users.map(this::mapUserToUserAdminSummaryDTO);
    }

    default Page<User> mapUserAdminSummaryDTOsToUsers(Page<UserAdminSummaryDTO> userAdminSummaryDTOs) {
        return userAdminSummaryDTOs.map(this::mapUserAdminSummaryDTOToUser);
    }
}
