package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.RoleAssignmentDTO;
import at.msm.asobo.dto.user.UserRolesDTO;
import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.services.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<String> getAllRoles() {
        return this.roleService.getAllRoles();
    }

    @PatchMapping("/assign")
    public UserRolesDTO assignRoles(@RequestBody RoleAssignmentDTO requestDTO) {
        return this.roleService.assignRoles(requestDTO.getUserId(), requestDTO.getRoles());
    }
}

