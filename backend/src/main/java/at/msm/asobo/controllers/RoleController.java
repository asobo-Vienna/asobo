package at.msm.asobo.controllers;

import at.msm.asobo.entities.Role;
import at.msm.asobo.entities.User;
import at.msm.asobo.exceptions.UserNotFoundException;
import at.msm.asobo.exceptions.RoleNotFoundException;
import at.msm.asobo.repositories.RoleRepository;
import at.msm.asobo.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class RoleController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleController(RoleRepository roleRepository,
                          UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .toList();
    }

    @PostMapping("/assign")
    public String assignRole(
            @RequestParam UUID userId,
            @RequestParam String roleName
    ) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("The specified role does not exist"));

        user.getRoles().add(role);
        userRepository.save(user);

        return "Role assigned";
    }

}

