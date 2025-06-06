package at.msm.asobo.controllers;


import at.msm.asobo.dto.admin.UserAdminDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("admin/users")
public class UserControllerAdmin {

    private final UserService userService;

    public UserControllerAdmin(UserService userService) {
        this.userService = userService;
    }

    /*@GetMapping
    public List<UserAdminDTO> getAllUsers() {
        //return this.userService.getAllUsers();
    }*/

    @GetMapping("/{id}")
    public UserAdminDTO getUserById(@PathVariable UUID id) {
        User foundUser = this.userService.getUserById(id);
        return new UserAdminDTO(foundUser);
    }

    @GetMapping("/{username}")
    public UserAdminDTO getUserByUsername(@PathVariable String username) {
        User foundUser = this.userService.getUserByUsername(username);
        return new UserAdminDTO(foundUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserAdminDTO createUser(@RequestBody @Valid User user) {
        User savedUser = this.userService.createUser(user);
        return new UserAdminDTO(savedUser);
    }

    // TODO create UserAdminService that returns UserAdminDTOs
//    @PutMapping("/{id}")
//    public UserAdminDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
//        User updatedUser = this.userService.updateUserById(id, userUpdateDTO);
//        return new UserAdminDTO(updatedUser);
//    }

    @DeleteMapping("/{id}")
    public UserAdminDTO deleteUser(@PathVariable UUID id) {
        User deletedUser = this.userService.deleteUserById(id);
        return new UserAdminDTO(deletedUser);
    }

}
