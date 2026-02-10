package at.msm.asobo.mappers;

import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.entities.Role;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO mapRoleToRoleDTO(Role role);

    Set<RoleDTO> mapRolesToRoleDTOs(Set<Role> roles);

    Role mapRoleDTOToRole(RoleDTO roleDTO);

    Set<Role> mapRoleDTOsToRoles(Set<RoleDTO> roleDTOs);
}
