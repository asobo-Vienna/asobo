package at.msm.asobo.mappers;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDTOToUserPublicDTOMapper {
    UserPublicDTO mapUserDTOToUserPublicDTO(UserDTO userDTO);
    List<UserPublicDTO> mapUserDTOsToUserPublicDTOs(List<UserDTO> userDTOs);
}
