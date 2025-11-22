package at.msm.asobo.controllers;

import at.msm.asobo.dto.comment.UserCommentWithEventTitleDTO;
import at.msm.asobo.dto.user.UserFullDTO;
import at.msm.asobo.services.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("isAuthenticated()")
// @Secured("ROLE_ADMIN")

public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserFullDTO> getAllUsers() {
        return this.adminService.getAllUsers();
    }

    @GetMapping("/comments")
    public List<UserCommentWithEventTitleDTO> getAllUserCommentsWithEventTitle() {
        return this.adminService.getAllUserCommentsWithEventTitle();
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

    /*@DeleteMapping("/{id}")
    public UserAdminDTO deleteUser(@PathVariable UUID id) {
        User deletedUser = this.userService.deleteUserById(id);
        return new UserAdminDTO(deletedUser);
    }*/

}
