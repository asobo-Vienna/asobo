package at.msm.asobo.services;

import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.dto.user.UserRolesDTO;
import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.repositories.RoleRepository;
import at.msm.asobo.mappers.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private static final String ROLE_SUPERADMIN = "SUPERADMIN";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserService userService;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, UserService userService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.userService = userService;
    }

    public List<RoleDTO> getAllRoles() {
        return this.roleRepository.findAll().stream()
                .map(this.roleMapper::mapRoleToRoleDTO)
                .collect(Collectors.toList());
    }

    // TODO: add unit test
    public Role getRoleByName(String roleName) {
        return this.roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));
    }

    public UserRolesDTO assignRoles(UUID userId, Set<RoleDTO> roles) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }

        User user = this.userService.getUserById(userId);

        Set<Role> roleEntities = roles.stream()
                .map(roleDTO -> this.roleRepository.findByName(roleDTO.getName())
                        .orElseThrow(() ->
                                new RoleNotFoundException("Role not found: " + roleDTO.getName())))
                .collect(Collectors.toSet());

        validateRoleHierarchy(roleEntities);

        user.setRoles(roleEntities);
        this.userService.saveUser(user);

        UserRolesDTO rolesDTO = new UserRolesDTO();
        rolesDTO.setUserId(user.getId());
        rolesDTO.setRoles(this.roleMapper.mapRolesToRoleDTOs(user.getRoles()));

        return rolesDTO;
    }

    private void validateRoleHierarchy(Set<Role> roles) {
        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        if (!roleNames.contains(ROLE_USER)) {
            throw new IllegalArgumentException("Every user requires the role USER");
        }

        if (roleNames.contains(ROLE_SUPERADMIN) &&
                (!roleNames.contains(ROLE_ADMIN) || !roleNames.contains(ROLE_USER))) {
            throw new IllegalArgumentException("SUPERADMIN requires ADMIN and USER roles");
        }

        if (roleNames.contains(ROLE_ADMIN) && !roleNames.contains(ROLE_USER)) {
            throw new IllegalArgumentException("ADMIN requires USER role");
        }
    }
}

