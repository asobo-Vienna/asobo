package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.medium.MediumWithEventTitleDTO;
import at.msm.asobo.dto.user.UserAdminSummaryDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.services.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/users/paginated")
    public Page<UserAdminSummaryDTO> getAllUsersPaginated(Pageable pageable) {
        return this.adminService.getAllUsersPaginated(pageable);
    }

    @GetMapping("/users")
    public List<UserFullDTO> getAllUsers() {
        return this.adminService.getAllUsers();
    }

    // TODO?: On expand get full user details for ONE user
    /*@GetMapping("/users/{id}/full")
    public UserFullDTO getUserWithDetails(@PathVariable UUID id);*/

    @GetMapping("/comments")
    public Page<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle(Pageable pageable) {
        return this.adminService.getAllUserCommentsWithEventTitle(pageable);
    }

    @GetMapping("/media")
    public Page<MediumWithEventTitleDTO> getAllMediaWithEventTitle(Pageable pageable) {
        return this.adminService.getAllMediaWithEventTitle(pageable);
    }


     /*@GetMapping
    public List<UserAdminDTO> getAllUsers() {
        //return this.userService.getAllUsers();
    }*/

    //@GetMapping("/{id}")
    /*public UserAdminDTO getUserById(@PathVariable UUID id) {
        User foundUser = this.userService.getUserById(id);
        return new UserAdminDTO(foundUser);
    }*/

    /*@GetMapping("/{username}")
    /*public UserAdminDTO getUserByUsername(@PathVariable String username) {
        User foundUser = this.userService.getUserByUsername(username);
        return new UserAdminDTO(foundUser);
    }*/

    /*@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAdminDTO createUser(@RequestBody @Valid User user) {
        User savedUser = this.userService.createUser(user);
        return new UserAdminDTO(savedUser);
    }*/

    // TODO create UserAdminService that returns UserAdminDTOs
//    @PutMapping("/{id}")
//    public UserAdminDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
//        User updatedUser = this.userService.updateUserById(id, userUpdateDTO);
//        return new UserAdminDTO(updatedUser);
//    }
}
