package at.msm.asobo.mappers;

import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.entities.Role;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO mapRoleToRoleDTO(Role role);
    Set<RoleDTO> mapRolesToRoleDTOs(Set<Role> roles);

    Role mapRoleDTOToRole(RoleDTO roleDTO);
    Set<Role> mapRoleDTOsToRoles(Set<RoleDTO> roleDTOs);
}
