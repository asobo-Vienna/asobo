package at.msm.asobo.services;

import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.dto.user.UserRolesDTO;
import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.repositories.RoleRepository;
import at.msm.asobo.repositories.UserRepository;
import at.msm.asobo.mappers.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDTO> getAllRoles() {
        return this.roleRepository.findAll().stream().map(this.roleMapper::mapRoleToRoleDTO).collect(Collectors.toList());
    }

    public UserRolesDTO assignRoles(UUID userId, Set<RoleDTO> roles) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Set<Role> roleEntities = roles.stream()
                .map(roleDTO -> roleRepository.findByName(roleDTO.getName())
                        .orElseThrow(() ->
                                new RoleNotFoundException("Role not found: " + roleDTO.getName())))
                .collect(Collectors.toSet());

        user.setRoles(roleEntities);
        userRepository.save(user);

        UserRolesDTO rolesDTO = new UserRolesDTO();
        rolesDTO.setUserId(user.getId());

        rolesDTO.setRoles(roleMapper.mapRolesToRoleDTOs(user.getRoles()));

        return rolesDTO;
    }
}

