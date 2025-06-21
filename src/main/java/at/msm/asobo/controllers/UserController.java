package at.msm.asobo.controllers;

import at.msm.asobo.dto.user.UserDTO;
import at.msm.asobo.dto.user.UserPublicDTO;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserPublicDTO> getAllUsers() {
        return this.userService.getAllUsers();
    }

    // we need "/id/ before the actual id, because otherwise
    // /{id} and /{username} lead to ambiguity
    @GetMapping("/id/{id}")
    public UserPublicDTO getUserById(@PathVariable UUID id) {
        return this.userService.getUserDTOById(id);
    }

    @GetMapping("/{username}")
    public UserPublicDTO getUserByUsername(@PathVariable String username) {
        return this.userService.getUserByUsername(username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPublicDTO registerUser(@ModelAttribute @Valid UserRegisterDTO registerDTO) {
        return this.userService.registerUser(registerDTO);
    }

    @PutMapping("/{id}")
    public UserPublicDTO updateUser(@PathVariable UUID id, @ModelAttribute @Valid UserUpdateDTO userUpdateDTO) {
        return this.userService.updateUserById(id, userUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public UserPublicDTO deleteUser(@PathVariable UUID id) {
        return this.userService.deleteUserById(id);
    }
}
