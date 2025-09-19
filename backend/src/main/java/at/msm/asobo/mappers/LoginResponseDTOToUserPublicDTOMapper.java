package at.msm.asobo.mappers;

import at.msm.asobo.dto.auth.LoginResponseDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginResponseDTOToUserPublicDTOMapper {
    UserPublicDTO mapLoginResponseDTOToUserPublicDTO(LoginResponseDTO loginResponseDTO);
}
