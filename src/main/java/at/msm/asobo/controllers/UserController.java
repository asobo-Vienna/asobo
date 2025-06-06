package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserRegisterDTO;
import at.msm.asobo.dto.user.UserUpdateDTO;
import at.msm.asobo.entities.User;
import at.msm.asobo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable UUID id) {
        User foundUser = this.userService.getUserById(id);
        return new UserDTO(foundUser);
    }

    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        User foundUser = this.userService.getUserByUsername(username);
        return new UserDTO(foundUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerUser(@RequestBody @Valid UserRegisterDTO registerDTO) {
        return this.userService.registerUser(registerDTO);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return this.userService.updateUserById(id, userUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public UserDTO deleteUser(@PathVariable UUID id) {
        User deletedUser = this.userService.deleteUserById(id);
        return new UserDTO(deletedUser);
    }
}
