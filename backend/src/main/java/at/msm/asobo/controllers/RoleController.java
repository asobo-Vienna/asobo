package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.RoleDTO;
import at.msm.asobo.dto.user.UserRolesDTO;
import at.msm.asobo.services.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return this.roleService.getAllRoles();
    }

    @PatchMapping("/assign")
    public UserRolesDTO assignRoles(@RequestBody UserRolesDTO userRolesDTO) {
        return this.roleService.assignRoles(userRolesDTO.getUserId(), userRolesDTO.getRoles());
    }
}

