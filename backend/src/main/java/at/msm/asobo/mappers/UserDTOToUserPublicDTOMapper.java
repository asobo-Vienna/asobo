package at.msm.asobo.mappers;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOToUserPublicDTOMapper {
  UserPublicDTO mapUserDTOToUserPublicDTO(UserDTO userDTO);

  List<UserPublicDTO> mapUserDTOsToUserPublicDTOs(List<UserDTO> userDTOs);
}
